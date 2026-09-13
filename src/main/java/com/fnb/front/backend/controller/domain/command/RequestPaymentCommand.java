package com.fnb.front.backend.controller.domain.command;

import com.fnb.front.backend.controller.domain.Order;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RequestPaymentCommand extends ApplicationEvent {
    private final Order order;

    @Builder
    public RequestPaymentCommand(Order source, Order order) {
        super(source);
        this.order = order;
    }
}
