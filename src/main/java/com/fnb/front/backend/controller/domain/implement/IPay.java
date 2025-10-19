package com.fnb.front.backend.controller.domain.implement;


import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.dto.KakaoPaymentApproveDto;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.dto.RequestCancelPaymentDto;

public interface IPay {
    RequestPaymentResponse request(RequestPayment requestPayment);
    void pay();
    ApprovePaymentResponse approve(KakaoPaymentApproveDto kakaoPaymentApproveDto);
    boolean cancel(RequestCancelPaymentDto cancelPaymentDto);
}
