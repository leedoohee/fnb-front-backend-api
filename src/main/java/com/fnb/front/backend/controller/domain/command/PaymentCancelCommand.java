package com.fnb.front.backend.controller.domain.command;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class PaymentCancelCommand extends ApplicationEvent {
    private final String transactionId;
    private final String payType;
    private final BigDecimal cancelAmount;
    private final BigDecimal cancelTaxFreeAmount;

    @Builder
    public PaymentCancelCommand(Object source, String transactionId, String payType, BigDecimal cancelAmount, BigDecimal cancelTaxFreeAmount) {
        super(source);
        this.transactionId = transactionId;
        this.payType = payType;
        this.cancelAmount = cancelAmount;
        this.cancelTaxFreeAmount = cancelTaxFreeAmount;
    }
}
