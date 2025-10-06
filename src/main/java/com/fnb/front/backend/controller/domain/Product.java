package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    // Monetary field with defined precision/scale
    @Column(name = "price", precision = 19, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "status")
    private String status;

    @Column(name = "min_quantity")
    private int minQuantity;

    @Column(name = "max_quantity")
    private int maxQuantity;

    @Column(name = "is_infinite_qty")
    private int isInfiniteQty;

    @Column(name = "product_type") // 1:일반, 2:세트
    private int productType;

    @Column(name = "sale_type") // 1:일반, 2:예약
    private int saleType;

    @Column(name = "is_take_out") // 1:포장, 0:매장
    private int isTakeOut;

    @Column(name = "is_delivery") // 1:배달, 0:매장
    private int isDelivery;

    @Column(name = "is_use") // 1:사용, 0:미사용
    private int isUse;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_available_coupon")
    private int isAvailableCoupon;

    @Column(name = "is_apply_membership")
    private int isApplyMembership;

    @Column(name = "apply_member_grades")
    private String applyMemberGrades;

    @Column(name = "apply_member_grade_dis_type")
    private String applyMemberGradeDisType;

    @Column(name = "apply_member_grade_dis_amt")
    private BigDecimal applyMemberGradeDisAmt;

    @Transient
    private List<ProductOption> productOption;

    @Transient
    private List<OrderAdditionalOption> orderAdditionalOptions;

    public Product() {

    }

    public boolean isAvailablePurchase() {
        return this.status.equalsIgnoreCase("available");
    }

    public boolean isAvailableUseCoupon() {
        return this.isAvailableCoupon == 1;
    }

    public boolean isLessMinPurchaseQuantity() {
        return quantity < this.minQuantity;
    }

    public boolean isOverMaxPurchaseQuantity() {
        return this.maxQuantity < this.quantity ;
    }

    public boolean inMemberShipDiscount(Member member) {
        List<String> grades = Arrays.stream(this.applyMemberGrades.split(",")).toList();
        return this.isApplyMembership == 1 && grades.contains(member.getGrade());
    }
}
