package com.fnb.front.backend.controller.domain;

import com.fnb.front.backend.controller.domain.implement.DiscountPolicy;

public class DiscountFactory {
    public static DiscountPolicy getPolicy(String calculateType) {
        if ("P".equals(calculateType)) {
            return new RateDiscount();
        } else if ("A".equals(calculateType)) {
            return new AbsoluteDiscount();
        }
        return null; // 또는 기본 전략 반환
    }
}