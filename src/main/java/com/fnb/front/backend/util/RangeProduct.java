package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum RangeProduct {
    ENTIRE("1"),
    PART("2");

    private final String value;

    RangeProduct(String value) {
        this.value = value;
    }
}
