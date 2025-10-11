package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "coupon_product")
public class CouponProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "coupon_id", nullable = false)
    private int couponId;

    @Column(name = "product_id", nullable = false)
    private int productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "is_apply_total_product") // 전체상품적용여부
    private String isApplyTotalProduct;

    @Column(name = "quantity")
    private int quantity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Coupon coupon;

    public CouponProduct() {

    }
}