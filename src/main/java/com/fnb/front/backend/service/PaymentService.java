package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.processor.PaymentProcessor;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.KakaoPayCancelResponse;
import com.fnb.front.backend.controller.dto.CancelPaymentDto;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.dto.ApprovePaymentDto;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.repository.*;
import com.fnb.front.backend.util.PayType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final AfterPaymentService afterPaymentService;

    public RequestPaymentResponse request(RequestPayment requestPayment) {
        PaymentProcessor paymentProcessor = new PaymentProcessor(PayFactory.getPay(requestPayment.getPayType()));
        return paymentProcessor.request(requestPayment);
    }

    public boolean approveKakaoResult(ApprovePaymentDto approvePaymentDto) {
        PaymentProcessor paymentProcessor   = new PaymentProcessor(PayFactory.getPay(PayType.KAKAO.getValue()));
        ApprovePaymentResponse response     = paymentProcessor.approve(approvePaymentDto);

        assert response != null : "결제 API 호출 결과가 실패하였습니다";

        return this.afterPaymentService.callPaymentProcess(response.getOrderId(), response, PayType.KAKAO.getValue());
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
}
