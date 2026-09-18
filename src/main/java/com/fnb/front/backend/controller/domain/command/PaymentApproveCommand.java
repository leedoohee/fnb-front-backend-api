package com.fnb.front.backend.controller.domain.command;

import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentApproveCommand {
    private final String payType;
    private final String orderId;
    private final ApprovePaymentResponse response;

    @Builder
    public PaymentApproveCommand(String payType, String orderId, ApprovePaymentResponse response) {
        this.payType = payType;
        this.orderId = orderId;
        this.response = response;
    }
}
