package com.fnb.front.backend.controller.domain.command;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RequestCancelCommand extends ApplicationEvent {
    private final String orderId;

    @Builder
    public RequestCancelCommand(Object source, String orderId) {
        super(source);
        this.orderId = orderId;
    }
}
