package com.fnb.backend.controller.domain.implement;

import java.math.BigDecimal;

public interface DiscountPolicy {
    BigDecimal calculate(BigDecimal price, BigDecimal discountAmount);
}