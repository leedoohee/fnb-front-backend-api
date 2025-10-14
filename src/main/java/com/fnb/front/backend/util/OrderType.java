package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum OrderType {
    PICKUP(0),    // 픽업
    DELIVERY(1);  // 배달

    private final int value;

    OrderType(int value) {
        this.value = value;
    }
}