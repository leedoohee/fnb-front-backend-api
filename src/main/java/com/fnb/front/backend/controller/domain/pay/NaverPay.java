package com.fnb.front.backend.controller.domain.pay;

import com.fnb.front.backend.controller.domain.implement.IPay;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.dto.CancelPaymentDto;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.dto.ApprovePaymentDto;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.dto.RequestCancelPaymentDto;

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
    public CancelPaymentDto cancel(RequestCancelPaymentDto cancelPaymentDto) {
        return null;
    }
}
