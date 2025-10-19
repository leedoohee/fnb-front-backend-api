package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Member;
import com.fnb.front.backend.controller.domain.MemberCoupon;
import com.fnb.front.backend.controller.domain.MemberPoint;
import com.fnb.front.backend.controller.dto.MemberAggregatesDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    @Transactional
    public void insertMember(Member member) {
        this.em.persist(member);
    }

    public Member findMember(String memberId) {

        CriteriaBuilder cb         = this.em.getCriteriaBuilder();
        CriteriaQuery<Member> cq   = cb.createQuery(Member.class);
        Root<Member> root          = cq.from(Member.class);

        cq = cq.where(cb.and(cb.equal(root.get("memberId"), memberId)));

        TypedQuery<Member> typedQuery = this.em.createQuery(cq);
        typedQuery.setMaxResults(1);

        return !typedQuery.getResultList().isEmpty() ? typedQuery.getSingleResult() : null;
    }

    public List<MemberPoint> findMemberPoints(String memberId) {

        CriteriaBuilder cb              = this.em.getCriteriaBuilder();
        CriteriaQuery<MemberPoint> cq   = cb.createQuery(MemberPoint.class);
        Root<MemberPoint> root          = cq.from(MemberPoint.class);

        cq = cq.where(cb.and(cb.equal(root.get("memberId"), memberId)));
        TypedQuery<MemberPoint> typedQuery = this.em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public MemberPoint findMemberPoint(String orderId) {

        CriteriaBuilder cb              = this.em.getCriteriaBuilder();
        CriteriaQuery<MemberPoint> cq   = cb.createQuery(MemberPoint.class);
        Root<MemberPoint> root          = cq.from(MemberPoint.class);

        cq = cq.where(cb.and(cb.equal(root.get("orderId"), orderId)));
        TypedQuery<MemberPoint> typedQuery = this.em.createQuery(cq);

        return typedQuery.getSingleResult();
    }

    public List<MemberCoupon> findMemberCoupons(String memberId, String isUsed) {
        List<Predicate> searchConditions = new ArrayList<>();

        CriteriaBuilder cb               = this.em.getCriteriaBuilder();
        CriteriaQuery<MemberCoupon> cq   = cb.createQuery(MemberCoupon.class);
        Root<MemberCoupon> root          = cq.from(MemberCoupon.class);

        searchConditions.add(cb.equal(root.get("memberId"), memberId));
        searchConditions.add(cb.equal(root.get("isUse"), isUsed));

        cq = cq.where(cb.and(searchConditions.toArray(new Predicate[0])));
        TypedQuery<MemberCoupon> typedQuery = this.em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public List<MemberCoupon> findMemberCoupons(String memberId, List<Integer> couponIds) {
        List<Predicate> searchConditions = new ArrayList<>();

        CriteriaBuilder cb               = this.em.getCriteriaBuilder();
        CriteriaQuery<MemberCoupon> cq   = cb.createQuery(MemberCoupon.class);
        Root<MemberCoupon> root          = cq.from(MemberCoupon.class);

        searchConditions.add(cb.equal(root.get("memberId"), memberId));
        searchConditions.add(root.get("couponId").in(couponIds));

        cq = cq.where(cb.and(searchConditions.toArray(new Predicate[0])));
        TypedQuery<MemberCoupon> typedQuery = this.em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public MemberAggregatesDto getMemberAggregates(String memberId, String isUsed) {

        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<MemberAggregatesDto> cq = cb.createQuery(MemberAggregatesDto.class);

        Root<Member> root = cq.from(Member.class);

        Join<Member, Order> orderJoin = root.join("orders", JoinType.LEFT);

        Join<Member, MemberCoupon> couponJoin = root.join("memberCoupons", JoinType.LEFT);
        couponJoin.on(cb.equal(couponJoin.get("isUsed"), isUsed));

        Join<Member, MemberPoint> pointJoin = root.join("memberPoints", JoinType.LEFT);
        pointJoin.on(cb.equal(pointJoin.get("isUsed"), isUsed));

        cq.where(cb.equal(root.get("memberId"), memberId));

        cq.select(cb.construct(
                MemberAggregatesDto.class,
                cb.countDistinct(orderJoin),
                cb.count(couponJoin),
                cb.sum(pointJoin.get("amount"))
        ));

        cq.groupBy(root.get("memberId"));

        try {
            return this.em.createQuery(cq).getSingleResult();
        } catch (NoResultException e) {
            // 결과가 없으면 모든 카운트/합계를 0으로 반환
            return new MemberAggregatesDto(0L, 0L, 0L);
        }
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

    public void updateLastLoginDate(String memberId) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaUpdate<Member> update = cb.createCriteriaUpdate(Member.class);
        Root<Member> root = update.from(Member.class);

        update.set("lastLoginDate", LocalDateTime.now());
        update.where(cb.and(cb.equal(root.get("memberId"), memberId)));

        this.em.createQuery(update).executeUpdate();
    }
}
