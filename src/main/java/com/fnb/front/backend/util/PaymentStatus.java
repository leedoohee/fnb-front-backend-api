package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    REQUEST("0"),
    APPROVE("1"),
    CANCEL("2");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }
}
