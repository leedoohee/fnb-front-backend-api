package com.fnb.front.backend.controller.domain.implement;


import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.domain.request.ApproveRequest;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.domain.request.CancelRequest;

public interface IPay {
    RequestPaymentResponse request(RequestPayment requestPayment);
    void pay();
    ApprovePaymentResponse approve(ApproveRequest approveRequest);
    boolean cancel(CancelRequest cancelRequest);
}
