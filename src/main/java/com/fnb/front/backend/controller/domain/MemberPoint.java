package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "member_point")
public class MemberPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "order_id", updatable = false, nullable = false)
    private String orderId;

    @Column(name = "point_type")
    private int pointType;

    @Column(name = "amount")
    private int amount;

    @Column(name = "is_used", nullable = false)
    private String isUsed;

    @Column(name = "member_id", updatable = false, nullable = false)
    private int memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public MemberPoint() {

    }
}
