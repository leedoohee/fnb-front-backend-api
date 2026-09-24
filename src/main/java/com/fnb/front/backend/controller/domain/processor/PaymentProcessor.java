package com.fnb.front.backend.controller.domain.processor;

import com.fnb.front.backend.controller.domain.implement.IPay;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.domain.request.ApproveRequest;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.domain.request.CancelRequest;

public class PaymentProcessor {

    private final IPay IPay;

    public PaymentProcessor(IPay IPay) {
        this.IPay = IPay;
    }

    public RequestPaymentResponse request(RequestPayment requestPayment) {
        return this.IPay.request(requestPayment);
    }

    public ApprovePaymentResponse approve(ApproveRequest approveRequest) {
        return this.IPay.approve(approveRequest);
    }

    public boolean cancel(CancelRequest cancelRequest) {
        return this.IPay.cancel(cancelRequest);
    }
}
