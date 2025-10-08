package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.MemberPoint;
import com.fnb.front.backend.controller.domain.Payment;
import com.fnb.front.backend.controller.domain.PaymentElement;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class PointRepository {

    private final EntityManager em;

    public PointRepository(EntityManager em) {
        this.em = em;
    }

    public void insertMemberPoint(MemberPoint memberPoint) {
        em.persist(memberPoint);
    }

}
