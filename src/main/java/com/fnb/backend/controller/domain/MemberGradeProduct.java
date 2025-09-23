package com.fnb.backend.controller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MemberGradeProduct {
    @Id
    private int id;
    private String grade;
    private int productId;
    private int addingPoint;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;
}
