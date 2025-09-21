package com.fnb.backend.controller.dto;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
public class KakaoPayApproveDto {
    private String cid;
    private String tid;
    @JsonProperty("partner_order_id")
    private String partnerOrderId;
    @JsonProperty("partner_user_id")
    private String partnerUserId;
    @JsonProperty("pg_token")
    private String pgToken;
}
