package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
public class Payment {

    @Id
    private int id;
    private BigDecimal amount;
    private String orderId;
    private Date paymentDate;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(columnDefinition = "paymentId")
    private List<PaymentElement> elements;

    public Payment() {

    }
}
