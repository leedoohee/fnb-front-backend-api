package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum Used {
    USED("0"),    // 픽업
    NOTUSED("1");  // 배달

    private final String value;

    Used(String value) {
        this.value = value;
    }
}