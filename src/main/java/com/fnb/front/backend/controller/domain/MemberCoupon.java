package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MemberCoupon {

    @Id
    private int id;
    private int memberId;
    private int couponId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couponId")
    private Coupon coupon;

    private int isUsed;

    public MemberCoupon() {

    }
}
