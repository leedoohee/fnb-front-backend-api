package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "payment_type")
public class PaymentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", updatable = false, nullable = false)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "is_used")
    private int isUsed;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public PaymentType() {

    }
}
