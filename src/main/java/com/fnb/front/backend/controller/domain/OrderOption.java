package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@Table(name = "order_additional_option")
public class OrderOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "option_type", nullable = false)
    private String optionType;

    @Column(name = "order_product_id", nullable = false)
    private int orderProductId;

    @Column(name = "option_id", nullable = false)
    private String optionId;

    @Column(name = "option_name")
    private String optionName;

    @Column(name = "price")
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_product_id")
    private OrderProduct orderProduct;

    public OrderOption() {

    }
}