package com.fnb.front.backend.controller.domain.event;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
@Builder
public class OrderStatusUpdateEvent extends ApplicationEvent {
    private final String orderId;
    private final String orderStatus;
}
