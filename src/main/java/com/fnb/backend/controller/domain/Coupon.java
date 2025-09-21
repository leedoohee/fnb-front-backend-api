package com.fnb.backend.controller.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Coupon {

    @Id
    private int id;
    private String couponName;
    private BigDecimal amount;
    private String couponType;
    private String discountType;
    private Date startDate;
    private Date endDate;
    private int applyProductId;

    @Transient
    private List<Integer> exceptProducts;

    private String applyGrades;
    private int isUsed;

    public boolean isCanApplyDuring() {
        Date now = new Date();
        return this.startDate.after(now) && this.endDate.before(now);
    }

    public boolean isAvailableStatus() {
        return this.isUsed == 1;
    }

    public boolean isBelongToAvailableGrade(Member member) {
        List<String> grades = Arrays.stream(applyGrades.split(",")).toList();
        return grades.contains(member.getGrade());
    }
}
