package com.fnb.front.backend.controller.domain.pay;

import com.fnb.front.backend.controller.domain.implement.IPay;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.CancelPaymentResponse;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.domain.request.ApproveRequest;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.domain.request.CancelRequest;

public class TossPay implements IPay {

    public TossPay() {

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
    public CancelPaymentResponse cancel(CancelRequest cancelPaymentDto) {
        return null;
    }
}
