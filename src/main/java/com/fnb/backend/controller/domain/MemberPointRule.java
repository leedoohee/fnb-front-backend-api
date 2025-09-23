package com.fnb.backend.controller.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class MemberPointRule {

    @Id
    private int id;
    private String grade;
    private String addingPointType;
    private BigDecimal addingPointAmount;
    private String applyUnit; // 상품 주문 총금액 or 결제 금액(쿠폰, 포인트제외)
    private BigDecimal minApplyAmount;
    private BigDecimal maxApplyAmount;

    //시작일자
    //종료일자 추가?

}
