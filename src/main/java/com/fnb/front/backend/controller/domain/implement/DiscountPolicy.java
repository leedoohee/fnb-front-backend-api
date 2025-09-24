package com.fnb.front.backend.controller.domain.implement;

import java.math.BigDecimal;

public interface DiscountPolicy {
    BigDecimal calculate(BigDecimal price, BigDecimal discountAmount);
}