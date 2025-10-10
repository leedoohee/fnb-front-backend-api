package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Payment;
import com.fnb.front.backend.controller.domain.PaymentElement;
import com.fnb.front.backend.controller.domain.PaymentType;
import com.fnb.front.backend.controller.domain.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentRepository {

    private final EntityManager em;

    public PaymentRepository(EntityManager em) {
        this.em = em;
    }

    public int insertPayment(Payment payment) {
        em.persist(payment);
        return payment.getId();
    }

    public void insertPaymentElement(PaymentElement paymentElement) {
        em.persist(paymentElement);
    }

    public List<PaymentType> findPaymentType() {
        CriteriaBuilder cb               = em.getCriteriaBuilder();
        CriteriaQuery<PaymentType> cq    = cb.createQuery(PaymentType.class);
        Root<PaymentType> root           = cq.from(PaymentType.class);

        cq = cq.select(root);

        TypedQuery<PaymentType> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

}
