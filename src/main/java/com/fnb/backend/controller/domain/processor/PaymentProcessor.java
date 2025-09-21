package com.fnb.backend.controller.domain.processor;

import com.fnb.backend.controller.domain.implement.IPay;
import com.fnb.backend.controller.domain.orderEvent.EnrollPaymentEvent;
import com.fnb.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.backend.controller.domain.response.CustomResponse;
import com.fnb.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.backend.controller.dto.ApprovePaymentDto;
import com.fnb.backend.controller.dto.RequestPaymentDto;

public class PaymentProcessor {

    private final IPay IPay;

    public PaymentProcessor(IPay IPay) {
        this.IPay = IPay;
    }

    public RequestPaymentResponse request(RequestPaymentDto requestPaymentDto) {
        return this.IPay.request(requestPaymentDto);
    }

    public ApprovePaymentResponse approve(ApprovePaymentDto approvePaymentDto) {
        return this.IPay.approve(approvePaymentDto);
    }

    public void cancel(EnrollPaymentEvent enrollPaymentEvent) {

    }
}
