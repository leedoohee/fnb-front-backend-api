package com.fnb.backend.controller.domain.orderEvent;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class EnrollPaymentEvent {
    private String payType;
    private String orderId;
    private String orderProductsName;
    private int paymentAmount;
    private String memberName;
    private String memberEmail;
    private String memberPhone;
    private String memberAddress;
}
