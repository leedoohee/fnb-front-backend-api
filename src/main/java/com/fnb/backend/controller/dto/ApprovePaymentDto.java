package com.fnb.backend.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApprovePaymentDto {
    private String paymentKey;
    private String paymentType;
    private String transactionId;
    private String memberName;
    private String orderId;
    private String pgToken;
    private long amount;
}