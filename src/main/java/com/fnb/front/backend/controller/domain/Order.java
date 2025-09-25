package com.fnb.front.backend.controller.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
public class Order {

    @Id
    private String orderId;

    private int memberId;
    private Date orderDate;
    private String orderStatus;
    private String orderType;
    private String merchantId;
    private BigDecimal usePoint;
    private BigDecimal useCouponAmount;
    private BigDecimal discountAmount;
    private BigDecimal memberShipAmount;
    private BigDecimal paymentAmount;
    private int quantity;

    @Transient
    List<OrderProduct> orderProducts;

    @Transient
    Payment payment;

    public Order() {

    }
}
