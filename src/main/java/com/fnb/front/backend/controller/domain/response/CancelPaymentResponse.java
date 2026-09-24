package com.fnb.front.backend.controller.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentResponse {
    private String cancellationId;
    private String transactionId;
    private String orderId;
    private String memberId;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private BigDecimal taxFree;
    private BigDecimal vat;
    private BigDecimal point;
    private BigDecimal discount;
    private BigDecimal greenDeposit;
}