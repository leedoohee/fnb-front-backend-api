package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Coupon;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CouponRepository {

    private final EntityManager em;

    public CouponRepository(EntityManager em) {
        this.em = em;
    }

    public Coupon findCoupon(int couponId) {
        CriteriaBuilder cb         = em.getCriteriaBuilder();
        CriteriaQuery<Coupon> cq   = cb.createQuery(Coupon.class);
        Root<Coupon> root          = cq.from(Coupon.class);

        cq = cq.where(cb.and(cb.equal(root.get("id"), couponId)));
        TypedQuery<Coupon> typedQuery = em.createQuery(cq);

        return typedQuery.getSingleResult();
    }

    public List<Coupon> findCoupons(List<Integer> couponIds) {

        CriteriaBuilder cb         = em.getCriteriaBuilder();
        CriteriaQuery<Coupon> cq   = cb.createQuery(Coupon.class);
        Root<Coupon> root          = cq.from(Coupon.class);

        cq = cq.where(cb.and(root.get("id").in(couponIds)));
        TypedQuery<Coupon> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }
}
