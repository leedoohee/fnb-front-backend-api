package com.fnb.front.backend.controller.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Point {

    @Id
    private int id;
    private int orderId;
    private int pointType;
    private BigDecimal amount;
    private int memberId;
}
