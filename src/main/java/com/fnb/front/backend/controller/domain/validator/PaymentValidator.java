package com.fnb.front.backend.controller.domain.validator;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.request.OrderCouponRequest;
import com.fnb.front.backend.controller.domain.request.OrderProductRequest;
import com.fnb.front.backend.controller.domain.request.OrderRequest;
import com.fnb.front.backend.util.OrderStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class PaymentValidator {

    public boolean isAvailableRequest(Order order, BigDecimal purchasePrice, BigDecimal vatAmount) {

        if (order.getTotalAmount().compareTo(purchasePrice) != 0) {
            return false;
        }

        if (order.getTotalAmount().divide(BigDecimal.valueOf(1.1), RoundingMode.HALF_EVEN).compareTo(vatAmount) != 0) {
            return false;
        }

        if (!OrderStatus.PENDING.getValue().equals(order.getOrderStatus()) && !OrderStatus.TEMP.getValue().equals(order.getOrderStatus())) {
            return false;
        }

        return true;
    }

    public boolean isEqualPrice(Order order, BigDecimal expectedPrice) {
        if (order.getTotalAmount().compareTo(expectedPrice) != 0) {
            return false;
        }

        return true;
    }
}
