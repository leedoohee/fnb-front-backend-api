package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    APPROVE("1"),
    CANCEL("2");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }
}
