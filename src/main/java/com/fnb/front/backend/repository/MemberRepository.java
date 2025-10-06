package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Member;
import com.fnb.front.backend.controller.domain.MemberCoupon;
import com.fnb.front.backend.controller.domain.Point;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberRepository {

    @Autowired
    private EntityManager em;

    public Member find(String id) {
        return em.createQuery("SELECT m FROM Member m WHERE m.memberId = :id", Member.class)
                .setParameter("memberId", id)
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
