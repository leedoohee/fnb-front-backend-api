package com.fnb.backend.controller.domain.implement;


import com.fnb.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.backend.controller.dto.ApprovePaymentDto;
import com.fnb.backend.controller.dto.CancelPaymentDto;
import com.fnb.backend.controller.dto.RequestPaymentDto;

public interface IPay {
    RequestPaymentResponse request(RequestPaymentDto requestPaymentDto);
    void pay();
    ApprovePaymentResponse approve(ApprovePaymentDto approvePaymentDto);
    void cancel(CancelPaymentDto cancelPaymentDto);
}
