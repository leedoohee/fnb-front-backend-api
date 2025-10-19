package com.fnb.front.backend.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayCancelDto {

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

    private ApprovedCancelAmount cancelAmount;

    //TODO 아래 두 객체는 미구현
    //canceled_amount
    //cancel_available_amount

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("approved_at")
    private String approvedAt;

    @JsonProperty("cancel_at")
    private String cancelAt;

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
        private int discount;
        @JsonProperty("green_deposit")
        private int greenDeposit;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovedCancelAmount {

        @JsonProperty("total")
        private int total;

        @JsonProperty("tax_free")
        private int taxFree;

        @JsonProperty("vat")
        private int vat;

        @JsonProperty("point")
        private int point;

        @JsonProperty("discount")
        private int discount;

        @JsonProperty("green_deposit")
        private int greenDeposit;
    }
}