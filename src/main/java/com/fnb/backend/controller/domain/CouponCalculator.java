package com.fnb.backend.controller.domain;

import com.fnb.backend.controller.domain.implement.DiscountPolicy;
import com.fnb.backend.controller.domain.implement.Calculator;

import java.math.BigDecimal;

public class CouponCalculator implements Calculator {

    private final Coupon coupon;
    private final DiscountPolicy discount;
    private final Product product;

    public CouponCalculator(Coupon coupon, Product product, DiscountPolicy discount) {
        this.coupon = coupon;
        this.product = product;
        this.discount = discount;
    }

    @Override
    public BigDecimal calculatePrice() {
        return this.discount.calculate(BigDecimal.valueOf(this.product.calculatePriceWithQuantity()), this.coupon.getAmount());
    }
}
