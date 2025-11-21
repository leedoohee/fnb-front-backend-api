package com.fnb.front.backend.controller.domain.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class PaymentCancelEvent extends ApplicationEvent {
    private final String transactionId;
    private final String payType;
    private final BigDecimal cancelAmount;
    private final BigDecimal cancelTaxFreeAmount;

    @Builder
    public PaymentCancelEvent(Object source, String transactionId, String payType, BigDecimal cancelAmount, BigDecimal cancelTaxFreeAmount) {
        super(source);
        this.transactionId = transactionId;
        this.payType = payType;
        this.cancelAmount = cancelAmount;
        this.cancelTaxFreeAmount = cancelTaxFreeAmount;
    }
}
