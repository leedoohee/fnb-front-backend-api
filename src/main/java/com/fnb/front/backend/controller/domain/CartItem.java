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
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "cart_id", updatable = false, nullable = false)
    private int cartId;

    @Column(name = "additional_option_id", updatable = false, nullable = false)
    private int additionalOptionId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public CartItem() {

    }
}
