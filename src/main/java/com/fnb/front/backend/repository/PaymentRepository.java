package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    public int insertPaymentCancel(PaymentCancel paymentCancel) {
        em.persist(paymentCancel);
        return paymentCancel.getId();
    }

    public int insertPaymentElement(PaymentElement paymentElement) {
        em.persist(paymentElement);

        return paymentElement.getId();
    }

    public List<PaymentType> findPaymentType() {
        CriteriaBuilder cb               = em.getCriteriaBuilder();
        CriteriaQuery<PaymentType> cq    = cb.createQuery(PaymentType.class);
        Root<PaymentType> root           = cq.from(PaymentType.class);

        cq = cq.select(root);

        TypedQuery<PaymentType> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public PaymentElement findPaymentElement(String transactionId) {
        CriteriaBuilder cb                  = em.getCriteriaBuilder();
        CriteriaQuery<PaymentElement> cq    = cb.createQuery(PaymentElement.class);
        Root<PaymentElement> root           = cq.from(PaymentElement.class);

        cq = cq.where(cb.and(cb.equal(root.get("transactionId"), transactionId)));

        TypedQuery<PaymentElement> typedQuery = em.createQuery(cq);

        return typedQuery.getSingleResult();
    }

    public Payment findPayment(int paymentId) {
        List<Predicate> searchConditions    = new ArrayList<>();
        CriteriaBuilder cb                  = em.getCriteriaBuilder();
        CriteriaQuery<Payment> cq           = cb.createQuery(Payment.class);
        Root<Payment> root                  = cq.from(Payment.class);

        Fetch<Payment, Order> orderFetch = root.fetch("order", JoinType.INNER);
        Fetch<Order, OrderProduct> orderProductFetch = orderFetch.fetch("orderProduct", JoinType.INNER);
        orderProductFetch.fetch("product", JoinType.INNER);
        Fetch<OrderProduct, Coupon> couponFetch      = orderProductFetch.fetch("coupon", JoinType.LEFT);
        couponFetch.fetch("memberCoupon", JoinType.INNER);

        cq = cq.select(root)
                .where(cb.and(cb.equal(root.get("paymentId"), paymentId)))
                .distinct(true);

        TypedQuery<Payment> typedQuery = em.createQuery(cq);

        return typedQuery.getSingleResult();
    }
}
