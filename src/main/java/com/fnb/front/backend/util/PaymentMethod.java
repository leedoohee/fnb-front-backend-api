package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    POINT("POINT"),
    CARD("CARD"),
    COUPON("COUPON"),
    BANK("BANK");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }
}
