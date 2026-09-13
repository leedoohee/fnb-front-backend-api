package com.fnb.front.backend.controller.domain.command;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RequestCancelCommand {
    private final String orderId;

    @Builder
    public RequestCancelCommand(String orderId) {
        this.orderId = orderId;
    }
}
