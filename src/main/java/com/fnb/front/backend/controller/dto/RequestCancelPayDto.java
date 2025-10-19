package com.fnb.front.backend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class RequestCancelPayDto {

    @NotNull
    private BigDecimal cancelAmount;

    @NotNull
    private BigDecimal cancelTaxFreeAmount;

    @NotBlank
    private String transactionId;
}
