package com.fnb.backend.controller.domain;

import com.fnb.backend.controller.domain.implement.Calculator;
import com.fnb.backend.controller.domain.implement.PointPolicy;
import java.math.BigDecimal;

public class PointCalculator implements Calculator {

    private final PointPolicy pointPolicy;
    private final BigDecimal price;
    private final BigDecimal applyAmount;

    public PointCalculator(BigDecimal price, BigDecimal amount, PointPolicy pointPolicy) {
        this.price = price;
        this.applyAmount = amount;
        this.pointPolicy = pointPolicy;
    }

    @Override
    public BigDecimal calculate() {
        return this.pointPolicy.calculate(this.price, this.applyAmount);
    }
}
