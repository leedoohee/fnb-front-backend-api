package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum PayType {
    KAKAO("KAKAO"),
    NAVER("NAVER"),
    TOSS("TOSS"),
    NON("NON");

    private final String value;

    PayType(String value) {
        this.value = value;
    }
}
