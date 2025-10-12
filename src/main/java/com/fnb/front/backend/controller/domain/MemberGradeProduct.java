package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "member_grade_product")
public class MemberGradeProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "grade", unique = true, nullable = false)
    private String grade;

    @Column(name = "product_id", unique = true, nullable = false)
    private int productId;

    @Column(name = "adding_point_type")
    private String addingPointType;

    @Column(name = "adding_point")
    private int addingPoint;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Product product;

    public MemberGradeProduct() {

    }
}
