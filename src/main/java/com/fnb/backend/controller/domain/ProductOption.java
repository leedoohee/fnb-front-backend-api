package com.fnb.backend.controller.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class ProductOption {
    @Id
    private String id;
    private String productId;
    private String name;
    private String description;
    private BigDecimal optionPrice;
}
