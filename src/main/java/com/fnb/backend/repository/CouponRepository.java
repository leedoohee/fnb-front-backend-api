package com.fnb.backend.repository;

import com.fnb.backend.controller.domain.Coupon;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CouponRepository {

    @Autowired
    private EntityManager em;

    public Coupon find(int id) {
        return em.createQuery("SELECT p FROM Coupon p WHERE p.id = :id", Coupon.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Coupon> findInIds(String ids) {
        return em.createQuery("SELECT c FROM Coupon c WHERE c.id in ( :ids )", Coupon.class)
                .setParameter("ids", ids)
                .getResultList();
    }
}
