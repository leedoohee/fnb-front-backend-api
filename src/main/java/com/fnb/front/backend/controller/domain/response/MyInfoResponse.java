package com.fnb.front.backend.controller.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyInfoResponse {
    private int memberId;
    private String memberName;
    private String address;
    private int ownedCouponCount;
    private int ownedPoint;
}
