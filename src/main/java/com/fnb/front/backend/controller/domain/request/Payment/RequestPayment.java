package com.fnb.front.backend.controller.domain.request.Payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class RequestPayment {
    private String payType;
    private String paymentKey;
    private String orderId;
    private String memberName;
    private String productName;
    private int quantity;
    private BigDecimal purchasePrice;
    private BigDecimal vatAmount;
    private BigDecimal taxAmount;
}
