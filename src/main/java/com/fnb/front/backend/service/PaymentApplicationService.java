package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.command.*;
import com.fnb.front.backend.controller.domain.implement.PaymentApprover;
import com.fnb.front.backend.controller.domain.processor.PaymentProcessor;
import com.fnb.front.backend.controller.domain.request.ApproveRequest;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.CancelPaymentResponse;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.domain.validator.PaymentValidator;
import com.fnb.front.backend.controller.domain.request.CancelRequest;
import com.fnb.front.backend.util.CommonUtil;
import com.fnb.front.backend.util.OrderStatus;
import com.fnb.front.backend.util.PayType;
import com.fnb.front.backend.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentApplicationService {
    private final PaymentService paymentService;

    private final OrderService orderService;

    private final PaymentCompleteService paymentCompleteService;

    private final PaymentValidator paymentValidator;

    private final PayFactory payFactory;

    public RequestPaymentResponse request(RequestPayment requestPayment, String memberId) {
        Order order = this.orderService.findMemberOrder(requestPayment.getOrderId(), memberId);
        String attemptKey = CommonUtil.generateAttemptKey();
        requestPayment.setAttemptKey(attemptKey);

        if (order == null) {
            throw new RuntimeException("주문 정보가 없습니다.");
        }

        boolean result = this.paymentValidator.isAvailableRequest(order, requestPayment.getPurchasePrice(),
                requestPayment.getVatAmount());

        if (!result) {
            throw new RuntimeException("결제 정합성 체크 과정에서 오류가 발생하였습니다.");
        }

        PaymentProcessor paymentProcessor   = new PaymentProcessor(this.payFactory.getPay(requestPayment.getPayType()));
        RequestPaymentResponse response     = paymentProcessor.request(requestPayment);

        this.paymentService.insertPaymentAttempt(PaymentAttempt.builder()
                .orderId(order.getOrderId())
                .memberId(memberId)
                .payType(requestPayment.getPayType())
                .attemptKey(attemptKey)
                .transactionId(response.getTransactionId())
                .expectedAmount(order.getTotalAmount())
                .expectedTaxFreeAmount(order.getTotalAmount().divide(BigDecimal.valueOf(1.1), RoundingMode.HALF_EVEN))
                .status(PaymentStatus.REQUEST.getValue())
                .createdAt(LocalDateTime.now())
                .build());

        return response;
    }

    public void approveKakaoResult(String pgToken, String attemptKey) {
        this.approvePayment(attemptKey, PayType.KAKAO, (attempt, order) -> {
            PaymentProcessor paymentProcessor = new PaymentProcessor(this.payFactory.getPay(PayType.KAKAO.getValue()));
            return paymentProcessor.approve(
                    ApproveRequest.builder()
                            .amount(attempt.getExpectedAmount())
                            .pgToken(pgToken)
                            .transactionId(attempt.getTransactionId())
                            .orderId(order.getOrderId())
                            .memberName(order.getMemberName())
                            .build()
            );
        });
    }

    @Transactional
    public void failKakaoResult(String attemptKey) {
        int count = paymentService.updateAttemptStatus(attemptKey, PaymentStatus.REQUEST.getValue(),
                PaymentStatus.AUTH_FAILED.getValue());

        if (count == 1) {
            return;
        }

        PaymentAttempt attempt = paymentService.findPaymentAttempt(attemptKey);

        if (attempt != null && PaymentStatus.AUTH_FAILED.getValue().equals(attempt.getAttemptKey())) {
            return;
        }

        throw new IllegalStateException("실패 처리할 수 없는 결제 시도입니다.");
    }

    public void handleRequestCancel(RequestCancelCommand command) {
        Order order = this.orderService.findMemberOrder(command.getOrderId(), command.getMemberId());

        if (order == null) {
            throw new RuntimeException("취소할 수 없는 주문입니다.");
        }

        Payment payment = this.paymentService.findPayment(command.getOrderId());

        if (!payment.getPaymentStatus().equals(PaymentStatus.APPROVE.getValue())) {
            throw new RuntimeException("취소할 수 없는 주문상태입니다.");
        }

        int count = this.paymentService.updatePaymentStatus(payment.getPaymentId(),
                PaymentStatus.APPROVE.getValue(), PaymentStatus.CANCELING.getValue());

        if (count == 0) {
            throw new RuntimeException("취소할 수 없는 주문상태입니다.");
        }

        List<PaymentElement> paymentElements = payment.getPaymentElements();

        PaymentElement paymentGateWayElement = paymentElements.stream()
                .filter(paymentElement -> StringUtils.hasText(paymentElement.getTransactionId()))
                .findFirst().orElse(null);

        if(paymentGateWayElement != null) {
            PaymentAttempt paymentAttempt = this.paymentService.findOrderPaymentAttempt(command.getOrderId(), payment.getAttemptKey());

            CancelPaymentResponse response = this.cancelPayment(paymentGateWayElement.getPaymentMethod(), paymentGateWayElement.getTransactionId(),
                    paymentGateWayElement.getAmount(), paymentGateWayElement.getTaxFree(),
                    paymentAttempt.getAttemptKey(), command.getOrderId());

            this.paymentCompleteService.handlePaymentCancel(AfterPaymentCancelCommand.builder()
                            .cancelPaymentResponse(response)
                            .orderId(response.getOrderId())
                            .paymentId(payment.getPaymentId()).build());

            this.paymentService.updateAttemptStatus(paymentAttempt.getAttemptKey(),
                    PaymentStatus.APPROVE.getValue(), PaymentStatus.CANCEL.getValue());
        } else {
            this.paymentCompleteService.handlePaymentCancel(AfterPaymentCancelCommand
                    .builder()
                    .cancelPaymentResponse(null)
                    .orderId(command.getOrderId())
                    .paymentId(payment.getPaymentId())
                    .build());
        }
    }

    private void approvePayment(String attemptKey, PayType payType, PaymentApprover approver) {
        ApprovePaymentResponse response;

        PaymentAttempt attempt = this.paymentService.findPaymentAttempt(attemptKey);

        if (attempt == null) {
            throw new RuntimeException("결제승인 과정에서 오류가 발생하였습니다.");
        }

        Order order = this.orderService.findMemberOrder(attempt.getOrderId(), attempt.getMemberId());

        if (order == null) {
            throw new RuntimeException("주문 정보가 존재하지 않습니다.");
        }

        int count = this.paymentService.updateAttemptStatus(attemptKey,
                PaymentStatus.REQUEST.getValue(), PaymentStatus.APPROVING.getValue());

        if (count == 0) {
            throw new RuntimeException("결제승인 과정에서 오류가 발생하였습니다.");
        }

        if (!this.paymentValidator.isEqualPrice(order, attempt.getExpectedAmount())) {
            this.paymentService.updateAttemptStatus(attemptKey, PaymentStatus.APPROVING.getValue(),
                    PaymentStatus.APPROVE_ERROR.getValue());

            throw new RuntimeException("실결제 금액과 요청 금액이 일치하지 않습니다.");
        }

        try {
            response = approver.approve(attempt, order);
        } catch (RuntimeException e) {
            updateAttemptStatus(attemptKey, PaymentStatus.APPROVING.getValue(), PaymentStatus.APPROVE_ERROR.getValue());

            throw e;
        } catch (Exception e) {
            updateAttemptStatus(attemptKey, PaymentStatus.APPROVING.getValue(), PaymentStatus.APPROVE_PENDING.getValue());

            throw new RuntimeException("PG 승인 결과를 확인할 수 없습니다.", e);
        }

        try {
            if (!this.paymentValidator.isEqualPrice(order, response.getTotalAmount())) {
                throw new RuntimeException("결제금액이 주문금액과 다릅니다.");
            }

            this.paymentCompleteService.handlePaymentApprove(PaymentApproveCommand
                    .builder()
                    .payType(attempt.getPayType())
                    .orderId(response.getOrderId())
                    .attemptKey(attemptKey)
                    .response(response)
                    .build());

        } catch (Exception completionException) {
            this.cancelPayment(
                    payType.getValue(),
                    response.getTransactionId(),
                    response.getTotalAmount(),
                    response.getTaxFree(),
                    attemptKey,
                    order.getOrderId()
            );

            throw completionException;
        }
    }

    private void updateAttemptStatus(String attemptKey, String expected, String next) {
        int updated = this.paymentService.updateAttemptStatus(attemptKey, expected, next);

        if (updated == 0) {
            throw new RuntimeException(expected + "에서 " + next + " 상태로 변경할 수 없습니다.");
        }
    }

    public void handleRequestPayment(RequestPaymentCommand command) {
        this.paymentCompleteService.handlePaymentApprove(PaymentApproveCommand
                .builder()
                .payType(null)
                .orderId(command.getOrderId())
                .response(null)
                .build());
    }

    private CancelPaymentResponse cancelPayment(String payType, String transactionId, BigDecimal cancelAmount,
                                     BigDecimal taxFree, String attemptKey, String orderId) {
        PaymentProcessor paymentProcessor  = new PaymentProcessor(this.payFactory.getPay(payType));

        CancelPaymentResponse response = paymentProcessor.cancel(CancelRequest.builder()
                .cancelAmount(cancelAmount)
                .cancelTaxFreeAmount(taxFree)
                .transactionId(transactionId).build());

        if (response.getOrderId() == null) {
            this.orderService.updateStatus(orderId, OrderStatus.PENDING.getValue());
            this.paymentService.updateAttemptStatus(attemptKey, PaymentStatus.APPROVING.getValue(),
                    PaymentStatus.CANCEL_PENDING.getValue());
        }

        return response;
    }
}
