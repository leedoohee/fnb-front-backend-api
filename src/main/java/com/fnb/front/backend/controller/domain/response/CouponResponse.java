package com.fnb.front.backend.controller.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CouponResponse {
    private int couponId;
    private String couponName;
    private String description;
    private String couponType;
    private String status;
    private LocalDateTime applyStartAt;
    private LocalDateTime applyEndAt;
}
