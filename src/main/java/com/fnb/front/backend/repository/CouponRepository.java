package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Coupon;
import com.fnb.front.backend.controller.domain.MemberCoupon;
import com.fnb.front.backend.controller.domain.MemberPoint;
import com.fnb.front.backend.controller.domain.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CouponRepository {

    private final EntityManager em;

    public CouponRepository(EntityManager em) {
        this.em = em;
    }

    public void insertMemberCoupon(MemberCoupon memberCoupon) {
        em.persist(memberCoupon);
    }

    public void updateUsedMemberCoupon(String memberId, int couponId) {
        List<Predicate> searchConditions    = new ArrayList<>();
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        CriteriaUpdate<MemberCoupon> update = cb.createCriteriaUpdate(MemberCoupon.class);
        Root<MemberCoupon> root = update.from(MemberCoupon.class);

        update.set("isUsed", "0");

        searchConditions.add(cb.equal(root.get("memberId"), memberId));
        searchConditions.add(cb.equal(root.get("couponId"), couponId));

        update.where(cb.and(searchConditions.toArray(new Predicate[0])));

        this.em.createQuery(update).executeUpdate();
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
