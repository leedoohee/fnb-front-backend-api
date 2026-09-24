package com.fnb.front.backend.controller.domain.pay;

import com.fnb.front.backend.controller.domain.implement.IPay;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.domain.request.ApproveRequest;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.domain.request.CancelRequest;

public class NaverPay implements IPay {

    public NaverPay() {

    }

    @Override
    public RequestPaymentResponse request(RequestPayment requestPayment) {
        return null;
    }

    @Override
    public void pay() {

    }

    @Override
    public ApprovePaymentResponse approve(ApproveRequest kakaoPaymentApproveDto) {
        return null;
    }

    @Override
    public boolean cancel(CancelRequest cancelPaymentDto) {
        return false;
    }
}
