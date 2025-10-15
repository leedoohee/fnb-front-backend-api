package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum PaymentType {
    APPROVE("1"),
    CANCEL("2");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }
}
