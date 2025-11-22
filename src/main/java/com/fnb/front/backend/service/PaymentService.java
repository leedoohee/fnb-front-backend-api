package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.event.PaymentCancelEvent;
import com.fnb.front.backend.controller.domain.event.RequestCancelEvent;
import com.fnb.front.backend.controller.domain.event.RequestPaymentEvent;
import com.fnb.front.backend.controller.domain.processor.PaymentProcessor;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.dto.KakaoPayCancelDto;
import com.fnb.front.backend.controller.dto.CancelPayDto;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.dto.KakaoPayApproveDto;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.dto.RequestCancelPayDto;
import com.fnb.front.backend.repository.*;
import com.fnb.front.backend.util.PayType;
import com.fnb.front.backend.util.PaymentMethod;
import com.fnb.front.backend.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final OrderService orderService;

    private final AfterPaymentService afterPaymentService;

    public RequestPaymentResponse request(RequestPayment requestPayment) {
        PaymentProcessor paymentProcessor = new PaymentProcessor(PayFactory.getPay(requestPayment.getPayType()));
        return paymentProcessor.request(requestPayment);
    }

    public void approveKakaoResult(KakaoPayApproveDto kakaoPaymentApproveDto) {
        PaymentProcessor paymentProcessor   = new PaymentProcessor(PayFactory.getPay(PayType.KAKAO.getValue()));
        ApprovePaymentResponse response     = paymentProcessor.approve(kakaoPaymentApproveDto);

        if (response == null) {
            throw new RuntimeException("결제승인 과정에서 오류가 발생하였습니다");
        }

        Order order = this.orderService.findOrder(response.getOrderId());
        //TODO 이벤트 패턴으로 바꾸기
        this.afterPaymentService.callPaymentProcess(order, response, PayType.KAKAO.getValue());
    }

    public void cancelKakaoResult(KakaoPayCancelDto response) {
        PaymentElement paymentElement   = this.paymentRepository.findPaymentElement(response.getTid());

        if (paymentElement == null) {
            throw new IllegalStateException("결제정보를 찾을 수 없습니다.");
        }

        Payment payment = this.paymentRepository.findPayment(paymentElement.getPaymentId());
        Order order     = this.orderService.findOrder(payment.getOrderId());

        //TODO 이벤트 패턴으로 바꾸기
        this.afterPaymentService.callCancelProcess(CancelPayDto.builder()
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
                .build()
                , order, payment);
    }

    private boolean cancel(String payType, String transactionId, BigDecimal cancelAmount, BigDecimal taxFree) {
        PaymentProcessor paymentProcessor  = new PaymentProcessor(PayFactory.getPay(payType));
        return paymentProcessor.cancel(RequestCancelPayDto.builder()
                .cancelAmount(cancelAmount)
                .cancelTaxFreeAmount(taxFree)
                .transactionId(transactionId).build());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handlePaymentCancelEvent(PaymentCancelEvent event) {
        boolean result = this.cancel(event.getPayType(),
                event.getTransactionId(),
                event.getCancelAmount(),
                event.getCancelTaxFreeAmount());

        if (!result) {
            throw new RuntimeException("결제취소 과정에서 오류가 발생하였습니다.");
        }
    }

    @TransactionalEventListener
    public void handleRequestCancelEvent(RequestCancelEvent event) {
        Payment payment = this.paymentRepository.findPayment(event.getOrderId());
        Order order     = this.orderService.findOrder(event.getOrderId());

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

        } else {
            this.afterPaymentService.callCancelProcess(null, order, payment);
        }
    }

    @TransactionalEventListener
    public void handleRequestPaymentEvent(RequestPaymentEvent event) {
        this.afterPaymentService.callPaymentProcess(event.getOrder(), null, null);
    }
}
