package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum OptionType {
    BASIC("BASIC"),
    ADDITIONAL("ADDITIONAL");

    private final String value;

    OptionType(String value) {
        this.value = value;
    }
}
