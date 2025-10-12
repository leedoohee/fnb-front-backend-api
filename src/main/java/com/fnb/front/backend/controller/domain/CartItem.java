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
@Table(name = "cart_item")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id", updatable = false, nullable = false)
    private int cartItemId;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", insertable=false, updatable=false)
    private Cart cart;

    // CartItem이 OPTION_ID 외래 키를 가집니다. (주인)
    @OneToOne(fetch = FetchType.LAZY) // N:1로 가정
    @JoinColumn(name = "option_id", insertable=false, updatable=false)
    private ProductOption productOption;

    public CartItem() {

    }
}
