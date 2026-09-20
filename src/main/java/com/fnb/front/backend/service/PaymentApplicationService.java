package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.command.*;
import com.fnb.front.backend.controller.domain.processor.PaymentProcessor;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.dto.CancelPayDto;
import com.fnb.front.backend.controller.dto.KakaoPayApproveDto;
import com.fnb.front.backend.controller.dto.KakaoPayCancelDto;
import com.fnb.front.backend.controller.dto.RequestCancelPayDto;
import com.fnb.front.backend.util.OrderStatus;
import com.fnb.front.backend.util.PayType;
import com.fnb.front.backend.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentApplicationService {
    private final PaymentService paymentService;

    private final OrderService orderService;

    private final PaymentCompleteService paymentCompleteService;

    public RequestPaymentResponse request(RequestPayment requestPayment, String memberId) {
        Order order = this.orderService.findMemberOrder(requestPayment.getOrderId(), memberId);
        String attemptKey = UUID.randomUUID().toString();
        requestPayment.setAttemptKey(attemptKey);

        if (order == null) {
            throw new RuntimeException("결제할 수 없는 주문입니다.");
        }

        if (order.getTotalAmount().compareTo(requestPayment.getPurchasePrice()) != 0) {
            throw new RuntimeException("결제요청금액이 주문금액과 다릅니다.");
        }

        if (order.getTotalAmount().divide(BigDecimal.valueOf(1.1), RoundingMode.HALF_EVEN).compareTo(requestPayment.getVatAmount()) != 0) {
            throw new RuntimeException("부가세금액이 주문금액과 다릅니다.");
        }

        if (!OrderStatus.PENDING.getValue().equals(order.getOrderStatus())) {
            throw new IllegalStateException("결제할 수 없는 주문상태입니다.");
        }

        PaymentProcessor paymentProcessor = new PaymentProcessor(PayFactory.getPay(requestPayment.getPayType()));
        RequestPaymentResponse response = paymentProcessor.request(requestPayment);

        if(response == null) {
            throw new RuntimeException("결제요청 과정에서 오류가 발생하였습니다.");
        }

        this.paymentService.insertPaymentAttempt(PaymentAttempt.builder()
                .orderId(order.getOrderId())
                .memberId(memberId)
                .payType(PayType.KAKAO.getValue())
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
        PaymentAttempt attempt = paymentService.findPaymentAttempt(attemptKey);

        if (attempt == null) {
            throw new RuntimeException("결제승인 과정에서 오류가 발생하였습니다.");
        }

        int claimed = paymentService.updateAttemptStatus(attemptKey);

        if (claimed == 0) {
            if (attempt.getStatus().equals(PaymentStatus.APPROVE.getValue())) {
                throw new IllegalStateException("이미 처리 중이거나 처리된 결제입니다.");
            }
        }

        Order order = this.orderService.findMemberOrder(attempt.getOrderId(), attempt.getMemberId());

        if (order == null) {
            throw new RuntimeException("결제승인 과정에서 오류가 발생하였습니다.");
        }

        if (order.getTotalAmount().compareTo(attempt.getExpectedAmount()) != 0) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }

        PaymentProcessor paymentProcessor = new PaymentProcessor(PayFactory.getPay(PayType.KAKAO.getValue()));
        ApprovePaymentResponse response   = paymentProcessor.approve(KakaoPayApproveDto.builder()
                .amount(order.getTotalAmount())
                .pgToken(pgToken)
                .paymentKey(attempt.getPayType())
                .paymentType(attempt.getPayType())
                .transactionId(attempt.getTransactionId())
                .orderId(order.getOrderId())
                .memberName(order.getMemberName())
                .build());

        if(response == null) {
            throw new RuntimeException("결제승인 과정에서 오류가 발생하였습니다.");
        }

        // 결제금액이 주문금액과 다를 경우 결제취소 처리 후 주문상태를 PENDING으로 변경
        if (order.getTotalAmount().compareTo(response.getTotalAmount()) != 0) {
            this.cancel(PayType.KAKAO.getValue(), response.getTransactionId(), response.getTotalAmount(), response.getTaxFree());
            this.orderService.updateStatus(order.getOrderId(), OrderStatus.PENDING.getValue());

            throw new RuntimeException("결제금액이 주문금액과 다릅니다.");
        }

        try {
            this.paymentCompleteService.handlePaymentApprove(PaymentApproveCommand
                    .builder()
                    .payType(PayType.KAKAO.getValue())
                    .orderId(response.getOrderId())
                    .response(response)
                    .build());

        } catch (Exception completionException) {
            this.cancel(PayType.KAKAO.getValue(), response.getTransactionId(), response.getTotalAmount(), response.getTaxFree());
            this.orderService.updateStatus(order.getOrderId(), OrderStatus.PENDING.getValue());
            throw completionException;
        }
    }

    public void cancelKakaoResult(KakaoPayCancelDto response) {
        PaymentElement paymentElement   = this.paymentService.findPaymentElement(response.getTid());

        assert paymentElement != null : "결제정보를 찾을 수 없습니다.";

        try {
            Payment payment = this.paymentService.findPayment(paymentElement.getPaymentId());
            Order order     = this.orderService.findOrder(payment.getOrderId());

            this.paymentCompleteService.handlePaymentCancel(AfterPaymentCancelCommand
                    .builder()
                    .cancelPayDto(CancelPayDto.builder()
                            .approvalId(Objects.requireNonNull(response).getAid())
                            .transactionId(response.getTid())
                            .productName(response.getItemName())
                            .quantity(response.getQuantity())
                            .totalAmount(response.getCancelAmount().getTotal())
                            .taxFree(response.getCancelAmount().getTaxFree())
                            .vat(response.getCancelAmount().getVat())
                            .point(response.getCancelAmount().getPoint())
                            .discount(response.getCancelAmount().getDiscount())
                            .greenDeposit(response.getCancelAmount().getGreenDeposit())
                            .approvedAt(LocalDateTime.parse(response.getApprovedAt()))
                            .cancelAt(LocalDateTime.parse(response.getCancelAt()))
                            .build())
                    .orderId(order.getOrderId())
                    .paymentId(payment.getPaymentId())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("결제취소 과정에서 오류가 발생하였습니다.");
        }
    }

    private boolean cancel(String payType, String transactionId, BigDecimal cancelAmount, BigDecimal taxFree) {
        PaymentProcessor paymentProcessor  = new PaymentProcessor(PayFactory.getPay(payType));
        return paymentProcessor.cancel(RequestCancelPayDto.builder()
                .cancelAmount(cancelAmount)
                .cancelTaxFreeAmount(taxFree)
                .transactionId(transactionId).build());
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

        List<PaymentElement> paymentElements = payment.getPaymentElements();

        //TODO payType으로 필터 ex)KAKAO, NAVER
        PaymentElement paymentGateWayElement = paymentElements.stream()
                .filter(paymentElement -> !paymentElement.getTransactionId().isEmpty())
                .findFirst().orElse(null);

        if(paymentGateWayElement != null) {
            boolean result = this.cancel(payment.getPaymentType(),
                                        paymentGateWayElement.getTransactionId(),
                                        paymentGateWayElement.getAmount(),
                                        paymentGateWayElement.getTaxFree());

            if (!result) {
                throw new RuntimeException("결제취소 과정에서 오류가 발생하였습니다.");
            }
        }

        this.paymentCompleteService.handlePaymentCancel(AfterPaymentCancelCommand
                .builder()
                .cancelPayDto(null)
                .orderId(command.getOrderId())
                .paymentId(payment.getPaymentId())
                .build());
    }

    public void handleRequestPayment(RequestPaymentCommand command) {
        this.paymentCompleteService.handlePaymentApprove(PaymentApproveCommand
                .builder()
                .payType(null)
                .orderId(command.getOrderId())
                .response(null)
                .build());
    }
}
