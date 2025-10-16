package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum PointType {
    PLUS(1),
    MINUS(0);

    private final int value;

    PointType(int value) {
        this.value = value;
    }
}
