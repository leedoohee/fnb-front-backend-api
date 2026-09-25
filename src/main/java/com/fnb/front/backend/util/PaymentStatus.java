package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    REQUEST("0"),
    APPROVING("1"),
    APPROVE("2"),
    CANCEL("3"),
    PENDING("4"),
    CANCEL_PENDING("5"),
    CANCELING("6"),
    APPROVE_ERROR("7");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }
}
