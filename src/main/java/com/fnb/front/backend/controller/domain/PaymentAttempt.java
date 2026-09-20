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
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_attempt")
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

    // 서버가 생성하는 외부 노출용 일회성 식별자
    @Column(name = "attempt_key", nullable = false)
    private String attemptKey;

    // 카카오 Ready 응답
    @Column(name = "transactionId", nullable = false)
    private String transactionId;

    @Column(name = "expectedAmount", nullable = false)
    private BigDecimal expectedAmount;

    @Column(name = "expectedTaxFreeAmount", nullable = false)
    private BigDecimal expectedTaxFreeAmount;

    @Column(name = "status", nullable = false)
    private String status; // READY, APPROVED, CANCELED, FAILED, EXPIRED

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    public PaymentAttempt() {

    }
}