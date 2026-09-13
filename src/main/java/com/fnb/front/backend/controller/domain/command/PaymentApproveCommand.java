package com.fnb.front.backend.controller.domain.command;

import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentApproveCommand extends ApplicationEvent {
    private final String payType;
    private final Order order;
    private final ApprovePaymentResponse response;

    @Builder
    public PaymentApproveCommand(Object source, String payType, Order order, ApprovePaymentResponse response) {
        super(source);
        this.payType = payType;
        this.order = order;
        this.response = response;
    }
}
