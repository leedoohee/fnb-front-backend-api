package com.fnb.front.backend.controller.domain.request.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCouponRequest {
    private int productId;
    private int couponId;
}
