package com.fnb.front.backend.controller.domain.command;

import com.fnb.front.backend.controller.domain.Order;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RequestPaymentCommand {
    private final String orderId;

    @Builder
    public RequestPaymentCommand(String orderId) {
        this.orderId = orderId;
    }
}
