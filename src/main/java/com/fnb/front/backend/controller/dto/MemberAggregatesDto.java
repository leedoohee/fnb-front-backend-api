package com.fnb.front.backend.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberAggregatesDto {
    private Long orderCount;   // 주문 건수
    private Long couponCount;  // 사용 가능한 쿠폰 개수
    private Long point;     // 사용 가능한 포인트 합계

    public MemberAggregatesDto(Long orderCount, Long couponCount, Long point) {
        this.orderCount = orderCount;
        this.couponCount = couponCount;
        this.point = point;
    }
}