package com.fnb.front.backend.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class KakaoPayCancelDto {

    private String cid;
    private String tid;
    private BigDecimal cancel_amount;
    private BigDecimal cancel_tax_free_amount;
}
