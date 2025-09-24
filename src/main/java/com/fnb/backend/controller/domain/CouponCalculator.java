package com.fnb.backend.controller.domain;

import com.fnb.backend.controller.domain.implement.DiscountPolicy;
import com.fnb.backend.controller.domain.implement.Calculator;

import java.math.BigDecimal;

public class CouponCalculator implements Calculator {

    private final Coupon coupon;
    private final DiscountPolicy discount;
    private final int price;

    public CouponCalculator(Coupon coupon, int price, DiscountPolicy discount) {
        this.coupon = coupon;
        this.price = price;
        this.discount = discount;
    }

    @Override
    public BigDecimal calculate() {
        return this.discount.calculate(BigDecimal.valueOf(this.price), this.coupon.getAmount());
    }
}
