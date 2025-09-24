package com.fnb.backend.controller.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
public class AdditionalOption {

    @Id
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private int productId;

    public AdditionalOption() {

    }
}
