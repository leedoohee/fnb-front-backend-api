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

    @Column(name = "option_type", updatable = false, nullable = false)
    private String optionType;

    @Column(name = "option_group_id", updatable = false, nullable = false)
    private int optionGroupId;

    @Column(name = "option_id", updatable = false, nullable = false)
    private int optionId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public CartItem() {

    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private ProductOption productOption;

}
