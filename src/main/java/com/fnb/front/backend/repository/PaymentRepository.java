package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
public class PaymentRepository {

    private final EntityManager em;

    public PaymentRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public int insertPayment(Payment payment) {
        this.em.persist(payment);
        return payment.getPaymentId();
    }

    @Transactional
    public int insertPaymentCancel(PaymentCancel paymentCancel) {
        this.em.persist(paymentCancel);
        return paymentCancel.getId();
    }

    @Transactional
    public void insertPaymentElement(PaymentElement paymentElement) {
        em.persist(paymentElement);
    }

    public List<PaymentType> findPaymentType() {
        CriteriaBuilder cb               = this.em.getCriteriaBuilder();
        CriteriaQuery<PaymentType> cq    = cb.createQuery(PaymentType.class);
        Root<PaymentType> root           = cq.from(PaymentType.class);

        cq = cq.select(root);

        TypedQuery<PaymentType> typedQuery = this.em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public PaymentElement findPaymentElement(String transactionId) {
        CriteriaBuilder cb                  = this.em.getCriteriaBuilder();
        CriteriaQuery<PaymentElement> cq    = cb.createQuery(PaymentElement.class);
        Root<PaymentElement> root           = cq.from(PaymentElement.class);

        cq = cq.where(cb.and(cb.equal(root.get("transactionId"), transactionId)));

        TypedQuery<PaymentElement> typedQuery = this.em.createQuery(cq);
        typedQuery.setMaxResults(1);

        return !typedQuery.getResultList().isEmpty() ? typedQuery.getSingleResult() : null;
    }

    public Payment findPayment(int paymentId) {
        CriteriaBuilder cb                  = this.em.getCriteriaBuilder();
        CriteriaQuery<Payment> cq           = cb.createQuery(Payment.class);
        Root<Payment> root                  = cq.from(Payment.class);

        root.fetch("paymentElements", JoinType.INNER);

        cq = cq.select(root)
                .where(cb.and(cb.equal(root.get("paymentId"), paymentId)))
                .distinct(true);

        TypedQuery<Payment> typedQuery = em.createQuery(cq);

        return typedQuery.getSingleResult();
    }

    public Payment findPayment(String orderId) {
        CriteriaBuilder cb                  = this.em.getCriteriaBuilder();
        CriteriaQuery<Payment> cq           = cb.createQuery(Payment.class);
        Root<Payment> root                  = cq.from(Payment.class);

        root.fetch("paymentElements", JoinType.INNER);

        cq = cq.select(root)
                .where(cb.and(cb.equal(root.get("orderId"), orderId)))
                .distinct(true);

        TypedQuery<Payment> typedQuery = em.createQuery(cq);

        return typedQuery.getSingleResult();
    }
}
