package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Payment;
import com.fnb.front.backend.controller.domain.PaymentElement;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class PaymentRepository {

    private final EntityManager em;

    public PaymentRepository(EntityManager em) {
        this.em = em;
    }

    public void insertPayment(Payment payment) {
        em.persist(payment);
    }

    public void insertPaymentElement(PaymentElement paymentElement) {
        em.persist(paymentElement);
    }

}
