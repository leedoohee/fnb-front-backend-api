package com.fnb.front.backend.controller.domain.implement;

import java.math.BigDecimal;

public interface PointPolicy {
    BigDecimal calculate(BigDecimal price, BigDecimal applyAmount);
}
