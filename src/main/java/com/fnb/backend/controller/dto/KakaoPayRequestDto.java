package com.fnb.backend.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder // Add this annotation
public class KakaoPayRequestDto {
    private String cid;
    private String partnerOrderId;
    private String partnerUserId;
    private String itemName;
    private int quantity;
    private BigDecimal totalAmount;
    private BigDecimal vatAmount;
    private BigDecimal taxFreeAmount;
    private String approvalUrl;
    private String failUrl;
    private String cancelUrl;
}