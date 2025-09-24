package com.fnb.front.backend.controller.domain.implement;


import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.dto.ApprovePaymentDto;
import com.fnb.front.backend.controller.dto.CancelPaymentDto;
import com.fnb.front.backend.controller.domain.request.Payment.RequestPayment;

public interface IPay {
    RequestPaymentResponse request(RequestPayment requestPayment);
    void pay();
    ApprovePaymentResponse approve(ApprovePaymentDto approvePaymentDto);
    void cancel(CancelPaymentDto cancelPaymentDto);
}
