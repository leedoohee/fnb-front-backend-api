package com.fnb.backend.controller.domain;

import com.fnb.backend.controller.domain.implement.DiscountPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RateDiscount implements DiscountPolicy {
    @Override
    public BigDecimal calculate(BigDecimal price, BigDecimal discountAmount) {
        // 비율 할인 로직
        return price.multiply(discountAmount.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP));
    }
}