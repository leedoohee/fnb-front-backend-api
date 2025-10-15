package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum MemberStatus {
    INACTIVE("0"),    // 픽업
    ACTIVE("1");  // 배달

    private final String value;

    MemberStatus(String value) {
        this.value = value;
    }
}
