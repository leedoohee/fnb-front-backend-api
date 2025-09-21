package com.fnb.backend.controller.domain.pay;

import com.fnb.backend.controller.domain.implement.IPay;
import com.fnb.backend.controller.domain.orderEvent.EnrollPaymentEvent;
import com.fnb.backend.controller.domain.response.CustomResponse;

public class NaverPay implements IPay {

    public NaverPay() {

    }

    @Override
    public CustomResponse enroll(EnrollPaymentEvent enrollPaymentEvent) {
        return null;
    }

    @Override
    public void pay(CustomResponse response) {

    }

    @Override
    public void approve() {

    }

    @Override
    public void cancel() {

    }
}
