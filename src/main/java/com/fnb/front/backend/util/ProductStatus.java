package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum ProductStatus {
    INACTIVE("INACTIVE"),
    PROCESSING("PROCESSING"),
    SALE("SALE");

    private final String value;

    ProductStatus(String value) {
        this.value = value;
    }
}
