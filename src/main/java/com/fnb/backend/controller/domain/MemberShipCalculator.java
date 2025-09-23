package com.fnb.backend.controller.domain;

import com.fnb.backend.controller.domain.implement.DiscountPolicy;
import com.fnb.backend.controller.domain.implement.Calculator;

import java.math.BigDecimal;

public class MemberShipCalculator implements Calculator {
    private final Member member;
    private final Product product;
    private final DiscountPolicy discount;

    public MemberShipCalculator(Member member, Product product, DiscountPolicy discount) {
        this.member = member;
        this.product = product;
        this.discount = discount;
    }

    @Override
    public BigDecimal calculate() {
        return this.discount.calculate(BigDecimal.valueOf(this.product.calculatePriceWithQuantity()), this.product.getApplyMemberGradeDisAmt());
    }
}
