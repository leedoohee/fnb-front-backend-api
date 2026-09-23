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
@AllArgsConstructor
@Builder
@Table(
        name = "payment_attempt",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_attempt_key",
                        columnNames = "attempt_key"
                ),
                @UniqueConstraint(
                        name = "uk_payment_attempt_tid",
                        columnNames = "transactionId"
                )
        }
)
public class PaymentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentAttemptId;

    @Column(name = "orderId", nullable = false)
    private String orderId;

    @Column(name="memberId", nullable = false)
    private String memberId;

    @Column(name="payType", nullable = false)
    private String payType;

    @Column(name = "attempt_key")
    private String attemptKey;

    @Column(name = "transactionId", nullable = false)
    private String transactionId;

    @Column(name = "expectedAmount", nullable = false)
    private BigDecimal expectedAmount;

    @Column(name = "expectedTaxFreeAmount", nullable = false)
    private BigDecimal expectedTaxFreeAmount;

    @Column(name = "status", nullable = false)
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    public PaymentAttempt() {

    }
}