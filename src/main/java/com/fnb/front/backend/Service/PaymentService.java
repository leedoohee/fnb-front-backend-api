package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.processor.PaymentProcessor;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.dto.CancelPaymentDto;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.dto.ApprovePaymentDto;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        PaymentProcessor paymentProcessor   = new PaymentProcessor(PayFactory.getPay("K"));
        ApprovePaymentResponse response     = paymentProcessor.approve(approvePaymentDto);

        assert response != null : "결제 API 호출 결과가 실패하였습니다";

        return this.afterPaymentService.callPaymentProcess(response.getOrderId(), response, "K");
    }

    public boolean cancelKakaoResult(CancelPaymentDto cancelPaymentDto) {
        PaymentElement paymentElement = this.paymentRepository.findPaymentElement(cancelPaymentDto.getTransactionId());

        if (paymentElement == null) {
            return true;
        }

        return this.afterPaymentService.callCancelProcess(cancelPaymentDto, paymentElement.getPaymentId());
    }
}
