package com.fnb.backend.controller.domain.pay;

import com.fnb.backend.controller.domain.implement.IPay;
import com.fnb.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.backend.controller.dto.ApprovePaymentDto;
import com.fnb.backend.controller.dto.CancelPaymentDto;
import com.fnb.backend.controller.domain.request.Payment.RequestPayment;

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
    public ApprovePaymentResponse approve(ApprovePaymentDto approvePaymentDto) {
        return null;
    }

    @Override
    public void cancel(CancelPaymentDto cancelPaymentDto) {

    }
}
