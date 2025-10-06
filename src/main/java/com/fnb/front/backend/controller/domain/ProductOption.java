package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "product_option")
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "option_id", nullable = false)
    private int optionId;

    @Column(name = "option_group_id", nullable = false, length = 36)
    private String optionGroupId; // UUID 사용을 가정하고 필드명 변경

    @Column(name = "option_type", nullable = false, length = 20)
    private String optionType;

    // Foreign Key reference to the Product entity
    @Column(name = "product_id", nullable = false)
    private int productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "is_use") // 1:사용, 0:미사용
    private int isUse;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}