package com.fnb.front.backend.controller.domain.event;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RequestCancelEvent extends ApplicationEvent {
    private final String orderId;

    @Builder
    public RequestCancelEvent(Object source, String orderId) {
        super(source);
        this.orderId = orderId;
    }
}
