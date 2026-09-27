package com.fnb.front.backend.controller.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossPayCancelRequest {
    private String cancelReason;
    private BigDecimal cancelAmount;
    private BigDecimal taxFreeAmount;
}

