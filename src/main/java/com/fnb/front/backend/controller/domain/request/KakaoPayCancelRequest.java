package com.fnb.front.backend.controller.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class KakaoPayCancelRequest {

    private String cid;
    private String tid;
    @JsonProperty("cancel_amount")
    private BigDecimal cancelAmount;
    @JsonProperty("cancel_tax_free_amount")
    private BigDecimal cancelTaxFreeAmount;
}
