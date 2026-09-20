package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "payment_cancel")
public class PaymentCancel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "payment_id", nullable = false)
    private int paymentId;

    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    @Column(name = "cancel_at", updatable = false)
    private LocalDateTime cancelAt;

    @Column(name = "cancel_status")
    private String cancelStatus;

    @Column(name = "cancel_amount", precision = 19, scale = 2)
    private BigDecimal cancelAmount;

    public PaymentCancel() {

    }
}
