package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.MemberPoint;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PointRepository {

    private final EntityManager em;

    public PointRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void insertMemberPoint(MemberPoint memberPoint) {
        this.em.persist(memberPoint);
    }

    public void deleteMemberPoint(String orderId) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        CriteriaDelete<MemberPoint> delete = cb.createCriteriaDelete(MemberPoint.class);
        Root<MemberPoint> root = delete.from(MemberPoint.class);

        delete = delete.where(cb.and(cb.equal(root.get("orderId"), orderId)));

        this.em.createQuery(delete).executeUpdate();
    }
}
