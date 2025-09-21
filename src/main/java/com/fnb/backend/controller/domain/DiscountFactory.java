package com.fnb.backend.controller.domain;

import com.fnb.backend.controller.domain.implement.DiscountPolicy;

public class DiscountFactory {
    public static DiscountPolicy getPolicy(String discountType) {
        if ("P".equals(discountType)) {
            return new RateDiscount();
        } else if ("A".equals(discountType)) {
            return new AbsoluteDiscount();
        }
        return null; // 또는 기본 전략 반환
    }
}