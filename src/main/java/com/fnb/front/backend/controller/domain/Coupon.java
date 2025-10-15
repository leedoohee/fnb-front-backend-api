package com.fnb.front.backend.controller.domain;

import com.fnb.front.backend.util.CouponStatus;
import com.fnb.front.backend.util.RangeProduct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@Entity
@Builder
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", updatable = false, nullable = false)
    private int couponId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "coupon_type")
    private String couponType;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "apply_start_at")
    private LocalDateTime applyStartAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "apply_end_at")
    private LocalDateTime applyEndAt;

    @Column(name = "min_apply_price", precision = 19, scale = 2)
    private BigDecimal minApplyPrice;

    @Column(name = "member_ship_grades")
    private String memberShipGrades;

    @Column(name = "available_quantity")
    private int availableQuantity;

    @Column(name = "used_quantity")
    private int usedQuantity;

    @Column(name = "status")
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "issued_type") // 자동 발급, 수동 발급
    private String issuedType;

    @Column(name = "apply_grades")
    private String applyGrades;

    @Column(name = "apply_entire_product")
    private String applyEntireProduct;

    @Transient
    private int applyProductId;

    @OneToMany(mappedBy = "coupon")
    private List<CouponProduct> couponProducts;

    @OneToOne(mappedBy = "coupon")
    private MemberCoupon memberCoupon;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couponId")
    private OrderProduct orderProduct;

    public Coupon() {

    }

    public boolean isCanApplyDuring() {
        LocalDateTime now = LocalDateTime.now();
        return this.applyStartAt.isAfter(now) && this.applyEndAt.isBefore(now);
    }

    public boolean isApplyToEntireProduct() {
        return this.applyEntireProduct.equals(RangeProduct.ENTIRE.getValue()); //1이면 전체
    }

    public boolean isAvailableStatus() {
        return this.status.equals(CouponStatus.AVAILABLE.getValue());
    }

    public boolean isBelongToAvailableGrade(Member member) {
        List<String> grades = Arrays.stream(applyGrades.split(",")).toList();
        return grades.contains(member.getGrade());
    }
}
