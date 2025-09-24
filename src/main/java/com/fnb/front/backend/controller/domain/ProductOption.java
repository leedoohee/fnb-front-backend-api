package com.fnb.front.backend.controller.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// 맛, 사이즈만
@Getter
@Setter
@Entity
public class ProductOption {
    @Id
    private int id;
    private String productId;
    private String name;
    private String description;
    private BigDecimal optionPrice;
}
