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

    // Foreign Key reference to the Order entity
    @Column(name = "order_id", unique = true, nullable = false) // Assuming one payment per order
    private String orderId;

    // Using LocalDateTime for precise date and time tracking
    @Column(name = "cancel_at", updatable = false)
    private LocalDateTime cancelAt;

    @Column(name = "cancel_status")
    private String cancelStatus;

    // Monetary fields with defined precision/scale for accurate currency storage
    @Column(name = "cancel_amount", precision = 19, scale = 2)
    private BigDecimal cancelAmount;

    public PaymentCancel() {

    }
}
