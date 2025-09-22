package com.fnb.backend.controller.request.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCouponRequest {
    private int productId;
    private int couponId;
}
