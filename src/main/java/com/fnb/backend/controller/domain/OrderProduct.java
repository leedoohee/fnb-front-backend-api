package com.fnb.backend.controller.domain;

import com.fnb.backend.controller.dto.CreateOrderProductDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String orderProductId;
    private String orderId;
    private int productId;
    private String name;
    private int couponId;
    private int quantity;
    private int originPrice;
    private int discountPrice;
    private int couponPrice;

    public OrderProduct() {

    }
}
