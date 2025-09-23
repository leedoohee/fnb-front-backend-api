package com.fnb.backend.controller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class MemberGrade {

    @Id
    private int id;
    private String grade;
    private String name;
    private String description;
    private int isMinOrderPrice;
    private int beingMonth;
    private int isExceptVat;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade")
    private MemberPointRule memberPointRule;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade")
    private List<MemberGradeProduct> products;
}
