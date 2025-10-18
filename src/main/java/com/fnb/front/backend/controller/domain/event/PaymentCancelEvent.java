package com.fnb.front.backend.controller.domain.event;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Builder
@Getter
public class PaymentCancelEvent extends ApplicationEvent {
    private String payType;
    private BigDecimal cancelAmount;
    private BigDecimal cancelTaxFreeAmount;
    private String transactionId;
}
