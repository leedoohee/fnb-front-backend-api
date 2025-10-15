package com.fnb.front.backend.controller.domain.processor;

import com.fnb.front.backend.controller.domain.implement.IPay;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.dto.CancelPaymentDto;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.dto.ApprovePaymentDto;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.dto.RequestCancelPaymentDto;

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

    public boolean cancel(RequestCancelPaymentDto cancelPaymentDto) {
        return this.IPay.cancel(cancelPaymentDto);
    }
}
