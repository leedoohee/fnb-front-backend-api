package com.fnb.front.backend.controller.domain;

import com.fnb.front.backend.controller.domain.implement.DiscountPolicy;

import java.math.BigDecimal;

public class AbsoluteDiscount implements DiscountPolicy {

    @Override
    public BigDecimal calculate(BigDecimal price, BigDecimal discountAmount) {
        // 절대 금액 할인 로직
        return discountAmount;
    }
}
