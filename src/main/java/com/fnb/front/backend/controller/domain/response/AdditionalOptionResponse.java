package com.fnb.front.backend.controller.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class AdditionalOptionResponse {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private int productId;
}
