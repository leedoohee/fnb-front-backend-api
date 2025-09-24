package com.fnb.backend.controller.domain;

import com.fnb.backend.controller.domain.implement.DiscountPolicy;
import com.fnb.backend.controller.domain.implement.Calculator;

import java.math.BigDecimal;

public class MemberShipCalculator implements Calculator {
    private final Member member;
    private final int price;
    private final int discountAmount;
    private final DiscountPolicy discount;

    public MemberShipCalculator(Member member, int price, int discountAmount, DiscountPolicy discount) {
        this.member = member;
        this.price = price;
        this.discountAmount = discountAmount;
        this.discount = discount;
    }

    @Override
    public BigDecimal calculate() {
        return this.discount.calculate(BigDecimal.valueOf(this.price), BigDecimal.valueOf(this.discountAmount));
    }
}
