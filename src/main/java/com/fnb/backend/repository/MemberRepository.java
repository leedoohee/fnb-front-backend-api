package com.fnb.backend.repository;

import com.fnb.backend.controller.domain.Member;
import com.fnb.backend.controller.domain.MemberCoupon;
import com.fnb.backend.controller.domain.Point;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberRepository {

    @Autowired
    private EntityManager em;

    public Member find(int id) {
        return em.createQuery("SELECT m FROM Member m WHERE m.id = :id", Member.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Point> findPointsById(int memberId) {
        return em.createQuery("SELECT p FROM Point p WHERE p.memberId = : memberId", Point.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<MemberCoupon> findMemberCouponsById(int memberId) {
        return em.createQuery("SELECT mc FROM MemberCoupon mc WHERE mc.memberId = :id and mc.isUsed = 1", MemberCoupon.class)
                .setParameter("id", memberId)
                .getResultList();
    }
}
