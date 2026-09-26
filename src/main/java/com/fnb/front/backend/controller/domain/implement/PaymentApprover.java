package com.fnb.front.backend.controller.domain.implement;

import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.PaymentAttempt;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;

@FunctionalInterface
public interface PaymentApprover {

    ApprovePaymentResponse approve(PaymentAttempt attempt, Order order);
}