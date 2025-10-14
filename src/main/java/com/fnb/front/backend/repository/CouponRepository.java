package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Coupon;
import com.fnb.front.backend.controller.domain.MemberCoupon;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FetchType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CouponRepository {

    private final EntityManager em;

    public CouponRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void insertMemberCoupon(MemberCoupon memberCoupon) {
        em.persist(memberCoupon);
    }

    public void updateUsedMemberCoupon(String memberId, int couponId, String isUsed) {
        List<Predicate> searchConditions    = new ArrayList<>();
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        CriteriaUpdate<MemberCoupon> update = cb.createCriteriaUpdate(MemberCoupon.class);
        Root<MemberCoupon> root = update.from(MemberCoupon.class);

        update.set("isUsed", isUsed);

        searchConditions.add(cb.equal(root.get("memberId"), memberId));
        searchConditions.add(cb.equal(root.get("couponId"), couponId));

        update.where(cb.and(searchConditions.toArray(new Predicate[0])));

        this.em.createQuery(update).executeUpdate();
    }

    public List<Coupon> findCoupons() {
        List<Predicate> searchConditions = new ArrayList<>();
        CriteriaBuilder cb               = em.getCriteriaBuilder();
        CriteriaQuery<Coupon> cq         = cb.createQuery(Coupon.class);
        Root<Coupon> root                = cq.from(Coupon.class);

        searchConditions.add(cb.equal(root.get("status"), "1"));
        searchConditions.add(cb.greaterThanOrEqualTo(root.get("applyStartAt"), LocalDateTime.now()));
        searchConditions.add(cb.lessThanOrEqualTo(root.get("applyEndAt"), LocalDateTime.now()));

        cq = cq.select(root)
                .where(cb.and(searchConditions.toArray(new Predicate[0])))
                .distinct(true);

        TypedQuery<Coupon> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public Coupon findCoupon(int couponId) {
        CriteriaBuilder cb         = em.getCriteriaBuilder();
        CriteriaQuery<Coupon> cq   = cb.createQuery(Coupon.class);
        Root<Coupon> root          = cq.from(Coupon.class);

        cq = cq.where(cb.and(cb.equal(root.get("couponId"), couponId)));
        TypedQuery<Coupon> typedQuery = em.createQuery(cq);
        typedQuery.setMaxResults(1);

        return !typedQuery.getResultList().isEmpty() ? typedQuery.getResultList().get(0) : null;
    }

    public MemberCoupon findMemberCoupon(String memberId, int couponId) {
        List<Predicate> searchConditions = new ArrayList<>();
        CriteriaBuilder cb               = em.getCriteriaBuilder();
        CriteriaQuery<MemberCoupon> cq   = cb.createQuery(MemberCoupon.class);
        Root<MemberCoupon> root          = cq.from(MemberCoupon.class);

        root.fetch("member", JoinType.INNER);
        Fetch<MemberCoupon, Coupon> couponFetch = root.fetch("coupon", JoinType.INNER);
        couponFetch.fetch("couponProduct", JoinType.INNER);

        searchConditions.add(cb.equal(root.get("memberId"), memberId));
        searchConditions.add(cb.equal(root.get("isUsed"), "1"));
        searchConditions.add(cb.equal(root.get("couponId"), couponId));

        cq = cq.select(root)
                .where(cb.and(searchConditions.toArray(new Predicate[0])))
                .distinct(true);

        TypedQuery<MemberCoupon> typedQuery = em.createQuery(cq);
        typedQuery.setMaxResults(1);

        return !typedQuery.getResultList().isEmpty() ? typedQuery.getResultList().get(0) : null;
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
