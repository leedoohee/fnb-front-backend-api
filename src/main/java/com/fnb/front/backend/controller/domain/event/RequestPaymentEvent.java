package com.fnb.front.backend.controller.domain.event;

import com.fnb.front.backend.controller.domain.Order;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
@Builder
public class RequestPaymentEvent extends ApplicationEvent {
    private final Order order;
}
