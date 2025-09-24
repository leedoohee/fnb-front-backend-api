package com.fnb.front.backend.controller.domain;

import com.fnb.front.backend.controller.domain.implement.PointPolicy;

import java.math.BigDecimal;

public class AbsolutePoint implements PointPolicy {

    @Override
    public BigDecimal calculate(BigDecimal price, BigDecimal applyAmount) {
        // 절대 금액 할인 로직
        return applyAmount;
    }
}
