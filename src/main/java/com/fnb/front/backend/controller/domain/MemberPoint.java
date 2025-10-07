package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "member_point")
public class MemberPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "order_id", updatable = false, nullable = false)
    private int orderId;

    @Column(name = "point_type")
    private int pointType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "is_used", nullable = false)
    private String isUsed;

    @Column(name = "member_id", updatable = false, nullable = false)
    private int memberId;
}
