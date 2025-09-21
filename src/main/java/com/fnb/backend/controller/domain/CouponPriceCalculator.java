package com.fnb.backend.controller.domain;

import com.fnb.backend.controller.domain.implement.DiscountPolicy;
import com.fnb.backend.controller.domain.implement.PriceCalculator;

import java.math.BigDecimal;

public class CouponPriceCalculator implements PriceCalculator {

    private final Coupon coupon;
    private final DiscountPolicy discount;
    private final Product product;

    public CouponPriceCalculator(Coupon coupon, Product product, DiscountPolicy discount) {
        this.coupon = coupon;
        this.product = product;
        this.discount = discount;
    }

    @Override
    public BigDecimal calculatePrice() {
        return this.discount.calculate(BigDecimal.valueOf(this.product.getTotalPrice()), this.coupon.getAmount());
    }
}
