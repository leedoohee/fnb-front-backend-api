package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Member;
import com.fnb.front.backend.controller.domain.MemberCoupon;
import com.fnb.front.backend.controller.domain.MemberPoint;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MemberRepository {

    private final EntityManager em;

    public MemberRepository(EntityManager em) {
        this.em = em;
    }

    public void insertMember(Member member) {
        em.persist(member);
    }

    public Member findMember(String memberId) {

        CriteriaBuilder cb         = em.getCriteriaBuilder();
        CriteriaQuery<Member> cq   = cb.createQuery(Member.class);
        Root<Member> root          = cq.from(Member.class);

        cq = cq.where(cb.and(cb.equal(root.get("memberId"), memberId)));
        TypedQuery<Member> typedQuery = em.createQuery(cq);

        return typedQuery.getSingleResult();
    }

    public List<MemberPoint> findMemberPoints(String memberId) {

        CriteriaBuilder cb              = em.getCriteriaBuilder();
        CriteriaQuery<MemberPoint> cq   = cb.createQuery(MemberPoint.class);
        Root<MemberPoint> root          = cq.from(MemberPoint.class);

        cq = cq.where(cb.and(cb.equal(root.get("memberId"), memberId)));
        TypedQuery<MemberPoint> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public MemberPoint findMemberPoint(String orderId) {

        CriteriaBuilder cb              = em.getCriteriaBuilder();
        CriteriaQuery<MemberPoint> cq   = cb.createQuery(MemberPoint.class);
        Root<MemberPoint> root          = cq.from(MemberPoint.class);

        cq = cq.where(cb.and(cb.equal(root.get("orderId"), orderId)));
        TypedQuery<MemberPoint> typedQuery = em.createQuery(cq);

        return typedQuery.getSingleResult();
    }

    public List<MemberCoupon> findMemberCoupons(String memberId) {
        List<Predicate> searchConditions    = new ArrayList<>();

        CriteriaBuilder cb               = em.getCriteriaBuilder();
        CriteriaQuery<MemberCoupon> cq   = cb.createQuery(MemberCoupon.class);
        Root<MemberCoupon> root          = cq.from(MemberCoupon.class);

        searchConditions.add(cb.equal(root.get("memberId"), memberId));
        searchConditions.add(cb.equal(root.get("isUse"), "1"));

        cq = cq.where(cb.and(searchConditions.toArray(new Predicate[0])));
        TypedQuery<MemberCoupon> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public List<MemberCoupon> findMemberCoupons(String memberId, List<Integer> couponIds) {
        List<Predicate> searchConditions    = new ArrayList<>();

        CriteriaBuilder cb               = em.getCriteriaBuilder();
        CriteriaQuery<MemberCoupon> cq   = cb.createQuery(MemberCoupon.class);
        Root<MemberCoupon> root          = cq.from(MemberCoupon.class);

        searchConditions.add(cb.equal(root.get("memberId"), memberId));
        searchConditions.add(root.get("couponId").in(couponIds));

        cq = cq.where(cb.and(searchConditions.toArray(new Predicate[0])));
        TypedQuery<MemberCoupon> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public void updateMinusPoint(String memberId, int point) {
        List<Predicate> searchConditions    = new ArrayList<>();
        CriteriaBuilder cb                  = this.em.getCriteriaBuilder();

        CriteriaUpdate<Member> update = cb.createCriteriaUpdate(Member.class);
        Root<Member> root = update.from(Member.class);

        Expression<Integer> currentPoints = root.get("points");
        Expression<Integer> newPoints     = cb.mod(currentPoints, point);

        update.set("points", newPoints);

        searchConditions.add(cb.equal(root.get("memberId"), memberId));

        update.where(cb.and(searchConditions.toArray(new Predicate[0])));

        this.em.createQuery(update).executeUpdate();
    }
}
