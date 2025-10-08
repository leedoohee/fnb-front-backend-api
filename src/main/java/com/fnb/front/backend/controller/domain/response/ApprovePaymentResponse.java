package com.fnb.front.backend.controller.domain.response;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovePaymentResponse {
    private String approvalId;
    private String transactionId;
    private String memberName;
    private String orderId;
    private String paymentMethod;
    private String productName;
    private int quantity;
    private BigDecimal totalAmount;
    private BigDecimal taxFree;
    private BigDecimal vat;
    private LocalDateTime approvedAt;
    private String isFreeInstall;
    private String binNumber;
    private String cardType;
    private String install;
    private String installType;
    private String cardCorp;
    private String cardCorpCode;
    private String issuer;
    private String issuerCode;
}