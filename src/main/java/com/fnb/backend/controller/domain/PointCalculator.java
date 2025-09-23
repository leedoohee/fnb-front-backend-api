package com.fnb.backend.controller.domain;

import com.fnb.backend.controller.domain.implement.Calculator;
import com.fnb.backend.controller.domain.implement.PointPolicy;
import java.math.BigDecimal;

public class PointCalculator implements Calculator {

    private final PointPolicy pointPolicy;
    private final Member member;

    public PointCalculator(Member member, PointPolicy pointPolicy) {
        this.member = member;
        this.pointPolicy = pointPolicy;
    }

    @Override
    public BigDecimal calculatePrice() {
        return null;
    }
}
