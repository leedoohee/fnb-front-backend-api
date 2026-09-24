package com.fnb.front.backend.controller.domain.validator;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.util.OrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PaymentValidator {

    public boolean isAvailableRequest(Order order, BigDecimal purchasePrice, BigDecimal vatAmount) {

        if (order.getTotalAmount().compareTo(purchasePrice) != 0) {
            return false;
        }

        if (order.getTotalAmount().divide(BigDecimal.valueOf(1.1), RoundingMode.HALF_EVEN).compareTo(vatAmount) != 0) {
            return false;
        }

        return OrderStatus.PENDING.getValue().equals(order.getOrderStatus()) || OrderStatus.TEMP.getValue().equals(order.getOrderStatus());
    }

    public boolean isEqualPrice(Order order, BigDecimal expectedPrice) {
        return order.getTotalAmount().compareTo(expectedPrice) == 0;
    }
}
