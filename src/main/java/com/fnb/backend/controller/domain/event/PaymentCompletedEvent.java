package com.fnb.backend.controller.domain.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Builder
@Getter
@Setter
public class PaymentCompletedEvent extends ApplicationEvent {
    private String orderId;
}
