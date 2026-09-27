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
public class TossPayApproveRequest {
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
}
