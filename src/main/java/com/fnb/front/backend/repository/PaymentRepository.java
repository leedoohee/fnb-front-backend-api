package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.OrderProduct;
import com.fnb.front.backend.controller.domain.Payment;
import com.fnb.front.backend.controller.domain.PaymentElement;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public class PaymentRepository {

    @Autowired
    private EntityManager em;

    public void insertPayment(Payment payment) {
        em.persist(payment);
    }

    public void insertPaymentElement(PaymentElement paymentElement) {
        em.persist(paymentElement);
    }

}
