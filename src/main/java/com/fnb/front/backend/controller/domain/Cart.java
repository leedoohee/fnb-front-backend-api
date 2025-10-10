package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "productId", updatable = false, nullable = false)
    private int productId;

    @Column(name = "member_id", updatable = false, nullable = false)
    private String memberId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "cart", cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Product product;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems;

    public Cart() {

    }
}
