package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@Table(name = "payment_element")
public class PaymentElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    // Foreign Key reference to the Payment entity
    @Column(name = "payment_id", nullable = false)
    private int paymentId;

    @Column(name = "payment_method", nullable = false) // 카드, 현금, 포인트 등
    private String paymentMethod;

    // Changing 'amount' to BigDecimal for accurate currency handling
    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "card_type") // 신용카드, 체크카드 등 (카드 결제 시)
    private String cardType;

    @Column(name = "card_number") // 카드 번호 (카드 결제 시). Consider encryption/masking.
    private String cardNumber;

    @Column(name = "bank_name") // 은행 이름 (계좌 이체 시)
    private String bankName;

    @Column(name = "account_number") // 계좌 번호 (계좌 이체 시)
    private String accountNumber;

    @Column(name = "account_type")
    private String accountType;

    // Using LocalDateTime for creation/update timestamps (Best Practice)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    public PaymentElement() {

    }
}
