package com.fnb.front.backend.controller.domain.command;

import com.fnb.front.backend.controller.domain.Order;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RequestPaymentCommand {
    private final Order order;

    @Builder
    public RequestPaymentCommand(Order order) {
        this.order = order;
    }
}
