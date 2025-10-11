package com.fnb.front.backend.controller.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCouponRequest {

    @NotNull
    private int productId;

    @NotNull
    private int couponId;
}
