package com.fnb.front.backend.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class KakaoPaymentApproveDto {
    private String paymentKey;
    private String paymentType;
    private String transactionId;
    private String memberName;
    private String orderId;
    private String pgToken;
    private BigDecimal amount;
}