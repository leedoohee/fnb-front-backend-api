package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@Table(name = "order_additional_option")
public class OrderAdditionalOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "order_product_id", nullable = false)
    private int orderProductId;

    @Column(name = "additional_option_id", nullable = false)
    private String additionalOptionId;

    @Column(name = "additional_option_name")
    private String additionalOptionName;

    @Column(name = "price")
    private int price;

    public OrderAdditionalOption() {

    }
}