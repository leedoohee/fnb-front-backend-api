package com.fnb.backend.controller.domain.pay;

import com.fnb.backend.controller.domain.implement.IPay;
import com.fnb.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.backend.controller.dto.ApprovePaymentDto;
import com.fnb.backend.controller.dto.CancelPaymentDto;
import com.fnb.backend.controller.dto.RequestPaymentDto;

public class TossPay implements IPay {

    public TossPay() {

    }

    @Override
    public RequestPaymentResponse request(RequestPaymentDto requestPaymentDto) {
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
