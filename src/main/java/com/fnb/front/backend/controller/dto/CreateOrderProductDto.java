package com.fnb.front.backend.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class CreateOrderProductDto {
    private String name;
    private int originPrice;
    private int purchasePrice;
    private int discountPrice;
    private int couponPrice;
    private int point;
    private int memberShipPrice;
    private int quantity;
    private int couponId;
    private int productId;
    private String orderId;
}
