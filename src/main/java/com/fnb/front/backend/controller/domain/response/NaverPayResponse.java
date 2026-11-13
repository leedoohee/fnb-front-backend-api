package com.fnb.front.backend.controller.domain.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverPayResponse {
    private String code;
    private String message;
    private Body body;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private String paymentId;
        private String payHistId;
        private String primaryPayMeans;
        private BigDecimal primaryPayCancelAmount;
        private BigDecimal primaryPayRestAmount;
        private BigDecimal npointCancelAmount;
        private BigDecimal npointRestAmount;
        private BigDecimal giftCardCancelAmount;
        private BigDecimal giftCardRestAmount;
        private BigDecimal discountCancelAmount;
        private BigDecimal discountRestAmount;
        private BigDecimal taxScopeAmount;
        private BigDecimal taxExScopeAmount;
        private BigDecimal taxScopeRestAmount;
        private BigDecimal taxExScopeRestAmount;
        private BigDecimal environmentDepositAmount;
        private BigDecimal environmentDepositRestAmount;
        private String cancelYmdt;
        private BigDecimal totalRestAmount;
        private BigDecimal applyRestAmount;
        private Detail detail;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Detail {
        private String paymentId;
        private String payHistId;
        private String merchantName;
        private String merchantId;
        private String merchantPayKey;
        private String merchantUserKey;
        private String admissionTypeCode;
        private String admissionYmdt;
        private String tradeConfirmYmdt;
        private String admissionState;
        private BigDecimal totalPayAmount;
        private BigDecimal applyPayAmount;
        private BigDecimal primaryPayAmount;
        private BigDecimal npointPayAmount;
        private BigDecimal giftCardAmount;
        private BigDecimal discountPayAmount;
        private BigDecimal taxScopeAmount;
        private BigDecimal taxExScopeAmount;
        private BigDecimal environmentDepositAmount;
        private String primaryPayMeans;
        private String cardCorpCode;
        private String cardNo;
        private String cardAuthNo;
        private Integer cardInstCount;
        private Boolean usedCardPoint;
        private String bankCorpCode;
        private String bankAccountNo;
        private String productName;
        private String merchantExtraParameter;
        private String merchantPayTransactionKey;
        private String userIdentifier;
        private String extraDeduction;
        private String useCfmYmdt;
    }
}