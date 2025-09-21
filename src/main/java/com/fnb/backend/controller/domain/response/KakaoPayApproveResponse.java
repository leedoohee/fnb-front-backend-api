package com.fnb.backend.controller.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayApproveResponse {

    private String aid;
    private String tid;
    private String cid;
    private String sid;
    @JsonProperty("partner_order_id")
    private String partnerOrderId;
    @JsonProperty("partner_user_id")
    private String partnerUserId;
    @JsonProperty("payment_method_type")
    private String paymentMethodType;
    private Amount amount;
    @JsonProperty("item_name")
    private String itemName;
    @JsonProperty("item_code")
    private String itemCode;
    private int quantity;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("approved_at")
    private String approvedAt;
    private String payload;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Amount {
        private BigDecimal total;
        @JsonProperty("tax_free")
        private BigDecimal taxFree;
        private BigDecimal vat;
        private BigDecimal point;
        private Discount discount;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Discount {
            private BigDecimal total;
        }
    }
}