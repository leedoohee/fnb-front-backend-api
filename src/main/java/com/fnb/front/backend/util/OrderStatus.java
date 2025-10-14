package com.fnb.front.backend.util;

import lombok.Getter;

@Getter
public enum OrderStatus {
    TEMP("0"),              // 임시 저장
    PAID("1"),              // 결제 완료
    DELIVERED("2"),    // 배송 완료
    ORDERED("3"),        // 주문 완료
    CANCELED("4");      // 주문 취th

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }
}