package com.fnb.front.backend.controller.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayApproveResponse {

    @JsonProperty("aid")
    private String aid;
    @JsonProperty("tid")
    private String tid;
    @JsonProperty("partner_order_id")
    private String partnerOrderId;
    @JsonProperty("partner_user_id")
    private String partnerUserId;
    @JsonProperty("payment_method_type")
    private String paymentMethodType;
    @JsonProperty("item_name")
    private String itemName;
    @JsonProperty("item_code")
    private String itemCode;
    @JsonProperty("quantity")
    private int quantity;

    private Amount amount;

    private CardInfo cardInfo;

    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("approved_at")
    private String approvedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Amount {
        @JsonProperty("total")
        private BigDecimal total;
        @JsonProperty("tax_free")
        private BigDecimal taxFree;
        @JsonProperty("vat")
        private BigDecimal vat;
        @JsonProperty("point")
        private BigDecimal point;
        @JsonProperty("discount")
        private Discount discount;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Discount {
            private BigDecimal total;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardInfo {
        @JsonProperty("interest_free_install")
        private String interestFreeInstall;
        @JsonProperty("bin")
        private String bin;
        @JsonProperty("card_type")
        private String cardType;
        @JsonProperty("install_month")
        private String installMonth;
        @JsonProperty("installment_type")
        private String installmentType;
        @JsonProperty("kakaopay_purchase_corp")
        private String kakaopayPurchaseCorp;
        @JsonProperty("kakaopay_purchase_corp_code")
        private String kakaopayPurchaseCorpCode;
        @JsonProperty("kakaopay_issuer_corp")
        private String kakaopayIssuerCorp;
        @JsonProperty("kakaopay_issuer_corp_code")
        private String kakaopayIssuerCorpCode;
    }
}