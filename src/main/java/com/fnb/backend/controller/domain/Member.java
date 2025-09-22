package com.fnb.backend.controller.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
public class Member {

    @Id
    private int id;
    private String name;
    private String grade;


    @Transient
    private List<MemberCoupon> ownedCoupon;

    @Transient
    private List<Point> points;

    private int status;

    public Member() {

    }

    public boolean isCanPurchase() {
        return status == 1;
    }

    public int getOwnedPointAmount() {
        return this.points.stream().map(point -> {
            if(point.getPointType() == 1) {
                return -1 * point.getAmount().intValue();
            } else {
                return point.getAmount().intValue();
            }
        }).mapToInt(Integer::intValue).sum();
    }
}
