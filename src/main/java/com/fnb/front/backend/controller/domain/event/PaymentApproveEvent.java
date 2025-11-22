package com.fnb.front.backend.controller.domain.event;

import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class PaymentApproveEvent extends ApplicationEvent {
    private final String payType;
    private final Order order;
    private final ApprovePaymentResponse response;

    @Builder
    public PaymentApproveEvent(Object source, String payType, Order order, ApprovePaymentResponse response) {
        super(source);
        this.payType = payType;
        this.order = order;
        this.response = response;
    }
}
