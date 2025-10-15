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
    @Column(name = "payment_element_id", updatable = false, nullable = false)
    private int paymentElementId;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus; //결제, 취소

    @Column(name = "payment_id", nullable = false)
    private int paymentId;

    @Column(name = "payment_method", nullable = false) // 카드, 현금, 포인트 등
    private String paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "tax_free", precision = 19, scale = 2, nullable = false)
    private BigDecimal taxFree;

    @Column(name = "vat", precision = 19, scale = 2, nullable = false)
    private BigDecimal vat;

    @Column(name = "approved_at", updatable = false)
    private LocalDateTime approvedAt;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "install")
    private String install;

    @Column(name = "is_free_install")
    private String isFreeInstall;

    @Column(name = "install_type")
    private String installType;

    @Column(name = "card_corp")
    private String cardCorp;

    @Column(name = "card_corp_code")
    private String cardCorpCode;

    @Column(name = "bin_number")
    private String binNumber;

    @Column(name = "issuer")
    private String issuer;

    @Column(name = "issuer_code")
    private String issuerCode;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", insertable = false, updatable = false)
    private Payment payment;

    public PaymentElement() {

    }
}
