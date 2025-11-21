package com.fnb.front.backend.controller.domain.event;

import com.fnb.front.backend.controller.domain.Order;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RequestPaymentEvent extends ApplicationEvent {
    private final Order order;

    @Builder
    public RequestPaymentEvent(Order source, Order order) {
        super(source);
        this.order = order;
    }
}
