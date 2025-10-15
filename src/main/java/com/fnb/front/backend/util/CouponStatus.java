package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum CouponStatus {
    INACTIVE("INACTIVE"),
    PROCESSING("PROCESSING"),
    AVAILABLE("AVAILABLE");

    private final String value;

    CouponStatus(String value) {
        this.value = value;
    }
}
