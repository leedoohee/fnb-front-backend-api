package com.fnb.front.backend.controller.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ProductOptionResponse {
    private int id;
    private int productId;
    private String name;
    private String description;
    private BigDecimal optionPrice;
}
