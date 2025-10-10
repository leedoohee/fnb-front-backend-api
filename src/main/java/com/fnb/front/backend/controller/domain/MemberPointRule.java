package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "member_point_rule")
public class MemberPointRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "grade", updatable = false, nullable = false)
    private String grade;

    @Column(name = "apply_point_type")
    private String addingPointType;

    @Column(name = "adding_point_amount", precision = 19, scale = 2)
    private BigDecimal addingPointAmount;

    @Column(name = "apply_unit")
    private String applyUnit;

    @Column(name = "min_apply_amount", precision = 19, scale = 2)
    private BigDecimal minApplyAmount;

    @Column(name = "max_apply_amount", precision = 19, scale = 2)
    private BigDecimal maxApplyAmount;

    //시작일자
    //종료일자 추가?
    @OneToOne(mappedBy = "memberPointRule")
    private MemberGrade memberGrade;

}
