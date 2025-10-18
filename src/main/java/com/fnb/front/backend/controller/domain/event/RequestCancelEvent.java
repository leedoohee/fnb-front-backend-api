package com.fnb.front.backend.controller.domain.event;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
@Builder
public class RequestCancelEvent extends ApplicationEvent {
    private String orderId;
}
