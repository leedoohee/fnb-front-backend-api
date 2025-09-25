package com.fnb.front.backend.controller.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
public class PaymentElement {

    @Id
    private int id;
    private int paymentId;
    private String paymentMethod;
    private int amount;
    private int couponId;
    private String cardNumber;
    private String cvv;
    private String expiryDate;
    private String issuer;

    public PaymentElement() {

    }
}
