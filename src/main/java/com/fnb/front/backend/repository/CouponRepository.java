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

    @Autowired
    private EntityManager em;

    public Coupon findCoupon(int couponId) {
        CriteriaBuilder cb         = em.getCriteriaBuilder();
        CriteriaQuery<Coupon> cq   = cb.createQuery(Coupon.class);
        Root<Coupon> root          = cq.from(Coupon.class);

        cq = cq.where(cb.and(cb.equal(root.get("id"), couponId)));
        TypedQuery<Coupon> typedQuery = em.createQuery(cq);

        return typedQuery.getSingleResult();
    }

    public List<Coupon> findInIds(String ids) {
        return em.createQuery("SELECT c FROM Coupon c WHERE c.id in ( :ids )", Coupon.class)
                .setParameter("ids", ids)
                .getResultList();
    }
}
