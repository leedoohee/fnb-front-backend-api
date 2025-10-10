package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@Table(name = "order_master")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    @Column(name = "member_seq", nullable = false)
    private int memberSeq;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(name = "order_date", updatable = false)
    private LocalDateTime orderDate;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "use_point", precision = 19, scale = 2)
    private BigDecimal usePoint;

    @Column(name = "order_type")
    private int orderType;

    @Column(name = "coupon_amount")
    private int couponAmount;

    @Column(name = "discount_amount", precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @OneToMany(mappedBy = "order")
    private List<OrderProduct> orderProducts;

    @Transient
    private Member member;

    @Transient
    private Payment payment;

    public Order() {

    }
}