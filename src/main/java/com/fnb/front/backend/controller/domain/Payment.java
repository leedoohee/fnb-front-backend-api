package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    // Foreign Key reference to the Order entity
    @Column(name = "order_id", unique = true, nullable = false) // Assuming one payment per order
    private String orderId;

    // Using LocalDateTime for precise date and time tracking
    @Column(name = "payment_at", updatable = false)
    private LocalDateTime paymentAt;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "payment_status")
    private String paymentStatus;

    // Monetary fields with defined precision/scale for accurate currency storage
    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "payment_amount", precision = 19, scale = 2)
    private BigDecimal paymentAmount;

    @OneToMany(mappedBy = "payment")
    private List<PaymentElement> paymentElements;

    @OneToOne(mappedBy = "order")
    private Order order;

    public Payment() {

    }
}