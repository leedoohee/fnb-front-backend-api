package com.fnb.front.backend.controller.domain.command;


import com.fnb.front.backend.controller.domain.response.CancelPaymentResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AfterPaymentCancelCommand {
    private final CancelPaymentResponse cancelPaymentResponse;
    private final String orderId;
    private final int paymentId;

    @Builder
    public AfterPaymentCancelCommand(CancelPaymentResponse cancelPaymentResponse, String orderId, int paymentId) {
        this.cancelPaymentResponse = cancelPaymentResponse;
        this.orderId = orderId;
        this.paymentId = paymentId;
    }
}
