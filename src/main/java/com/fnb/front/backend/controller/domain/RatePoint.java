package com.fnb.front.backend.controller.domain;

import com.fnb.front.backend.controller.domain.implement.PointPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RatePoint implements PointPolicy {
    @Override
    public BigDecimal calculate(BigDecimal price, BigDecimal applyAmount) {
        // 비율 할인 로직
        return price.multiply(applyAmount.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP));
    }
}