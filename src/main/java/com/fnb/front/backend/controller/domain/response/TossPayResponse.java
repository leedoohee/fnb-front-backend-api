package com.fnb.front.backend.controller.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossPayResponse {
    private String paymentKey;
    private String orderId;
    private String orderName;
    private String method;
    private String status;
    private String lastTransactionKey;
    private String approvedAt;
    private BigDecimal totalAmount;
    private BigDecimal balanceAmount;
    private BigDecimal suppliedAmount;
    private BigDecimal vat;
    private BigDecimal taxFreeAmount;
    private Card card;
    private List<Cancel> cancels;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Card {
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private Integer installmentPlanMonths;
        private Boolean isInterestFree;
        private String cardType;
        private String ownerType;
        private String acquireStatus;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cancel {
        private BigDecimal cancelAmount;
        private String cancelReason;
        private BigDecimal taxFreeAmount;
        private BigDecimal taxExemptionAmount;
        private BigDecimal refundableAmount;
        private BigDecimal easyPayDiscountAmount;
        private String canceledAt;
        private String transactionKey;
        private String cancelStatus;
        private String cancelRequestId;
    }
}

