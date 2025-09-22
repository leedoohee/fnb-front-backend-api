package com.fnb.backend.controller.domain.processor;

import com.fnb.backend.controller.domain.implement.IPay;
import com.fnb.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.backend.controller.dto.ApprovePaymentDto;
import com.fnb.backend.controller.domain.request.Payment.RequestPayment;

public class PaymentProcessor {

    private final IPay IPay;

    public PaymentProcessor(IPay IPay) {
        this.IPay = IPay;
    }

    public RequestPaymentResponse request(RequestPayment requestPayment) {
        return this.IPay.request(requestPayment);
    }

    public ApprovePaymentResponse approve(ApprovePaymentDto approvePaymentDto) {
        return this.IPay.approve(approvePaymentDto);
    }
}
