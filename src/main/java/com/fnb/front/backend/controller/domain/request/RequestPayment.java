package com.fnb.front.backend.controller.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class RequestPayment {

    @NotBlank
    private String payType;

    @NotBlank
    private String paymentKey;

    @NotBlank
    private String orderId;

    @NotBlank
    private String memberName;

    @NotBlank
    private String productName;

    @NotNull
    private int quantity;

    @NotNull
    private BigDecimal purchasePrice;

    @NotNull
    private BigDecimal vatAmount;

    @NotNull
    private BigDecimal taxAmount;
}
