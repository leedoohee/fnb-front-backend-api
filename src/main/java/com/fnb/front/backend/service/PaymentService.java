package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.event.PaymentCancelEvent;
import com.fnb.front.backend.controller.domain.event.RequestCancelEvent;
import com.fnb.front.backend.controller.domain.event.RequestPaymentEvent;
import com.fnb.front.backend.controller.domain.processor.PaymentProcessor;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.KakaoPayCancelResponse;
import com.fnb.front.backend.controller.dto.CancelPaymentDto;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.dto.ApprovePaymentDto;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.dto.RequestCancelPaymentDto;
import com.fnb.front.backend.repository.*;
import com.fnb.front.backend.util.PayType;
import com.fnb.front.backend.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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

    public boolean approveKakaoResult(ApprovePaymentDto approvePaymentDto) {
        PaymentProcessor paymentProcessor   = new PaymentProcessor(PayFactory.getPay(PayType.KAKAO.getValue()));
        ApprovePaymentResponse response     = paymentProcessor.approve(approvePaymentDto);

        assert response != null : "결제 API 호출 결과가 실패하였습니다";

        return this.afterPaymentService.callPaymentProcess(this.orderService.findOrder(response.getOrderId()), response, PayType.KAKAO.getValue());
    }

    public boolean cancelKakaoResult(KakaoPayCancelResponse response) {
        PaymentElement paymentElement = this.paymentRepository.findPaymentElement(response.getTid());

        if (paymentElement == null) {
            return true;
        }

        return this.afterPaymentService.callCancelProcess(CancelPaymentDto.builder()
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
                .build(), paymentElement.getPaymentId());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePaymentCancelEvent(PaymentCancelEvent event) {
        PaymentProcessor paymentProcessor  = new PaymentProcessor(PayFactory.getPay(event.getPayType()));
        boolean result = paymentProcessor.cancel(RequestCancelPaymentDto.builder()
                .cancelAmount(event.getCancelAmount())
                .cancelTaxFreeAmount(event.getCancelTaxFreeAmount())
                .transactionId(event.getTransactionId()).build());

        if (!result) {
            throw new RuntimeException("결제취소 과정에서 오류가 발생하였습니다.");
        }
    }

    @TransactionalEventListener
    public void handleRequestCancelEvent(RequestCancelEvent event) {
        Payment payment = this.paymentRepository.findPayment(event.getOrderId());

        assert payment.getPaymentStatus().equals(PaymentStatus.APPROVE.getValue()): "취소할 수 없는 결제상태입니다.";

        List<PaymentElement> paymentElements = payment.getPaymentElements();

        PaymentElement paymentGateWayElement = paymentElements.stream()
                .filter(paymentElement -> !paymentElement.getTransactionId().isEmpty())
                .findFirst().orElse(null);

        if(paymentGateWayElement != null) {
            PaymentProcessor paymentProcessor  = new PaymentProcessor(PayFactory.getPay(payment.getPaymentType()));
            boolean result = paymentProcessor.cancel(RequestCancelPaymentDto.builder()
                    .cancelAmount(paymentGateWayElement.getAmount())
                    .cancelTaxFreeAmount(paymentGateWayElement.getTaxFree())
                    .transactionId(paymentGateWayElement.getTransactionId()).build());

            if (!result) {
                throw new RuntimeException("결제취소 과정에서 오류가 발생하였습니다.");
            }

        } else {
            this.afterPaymentService.callCancelProcess(null, payment.getPaymentId());
        }
    }

    @TransactionalEventListener
    public void handleRequestPaymentEvent(RequestPaymentEvent event) {
        this.afterPaymentService.callPaymentProcess(event.getOrder(), null, null);
    }
}
