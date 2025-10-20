package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.MemberCoupon;
import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.OrderProduct;
import com.fnb.front.backend.controller.domain.request.MyPageRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderRepository {

    private final EntityManager em;

    public OrderRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void insertOrder(Order order) {
        this.em.persist(order);
    }

    @Transactional
    public void insertOrderProducts(List<OrderProduct> orderProducts) {
        this.em.persist(orderProducts);
    }

    public void updateOrderStatus(String orderId, String status) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        CriteriaUpdate<Order> update = cb.createCriteriaUpdate(Order.class);
        Root<Order> root = update.from(Order.class);

        update.set("status", status);
        update.where(cb.and(cb.equal(root.get("orderId"), orderId)));

        this.em.createQuery(update).executeUpdate();
    }

    public Long findTotalOrderCount(MyPageRequest orderRequest) {
        CriteriaBuilder cb          = this.em.getCriteriaBuilder();
        CriteriaQuery<Long> cq      = cb.createQuery(Long.class);
        Root<Order> root            = cq.from(Order.class);

        cq = cq.where(cb.and(this.buildConditions(orderRequest, cb, root).toArray(new Predicate[0])));
        cq = cq.select((cb.count(root)));

        return this.em.createQuery(cq).getSingleResult();
    }

    public List<OrderProduct> findOrderProducts(String orderId) {
        CriteriaBuilder cb               = this.em.getCriteriaBuilder();
        CriteriaQuery<OrderProduct> cq   = cb.createQuery(OrderProduct.class);
        Root<OrderProduct> root          = cq.from(OrderProduct.class);

        root.join("product", JoinType.INNER);
        root.join("coupon", JoinType.LEFT);

        cq = cq.select(root)
                .where(cb.and(cb.equal(root.get("orderId"), orderId)))
                .distinct(true);

        TypedQuery<OrderProduct> typedQuery = this.em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public Order findOrder(String orderId) {
        CriteriaBuilder cb        = this.em.getCriteriaBuilder();
        CriteriaQuery<Order> cq   = cb.createQuery(Order.class);
        Root<Order> root          = cq.from(Order.class);

        root.join("member", JoinType.INNER);
        root.join("orderProducts", JoinType.INNER);

        cq = cq.select(root)
                .where(cb.and(cb.equal(root.get("orderId"), orderId)))
                .distinct(true);

        TypedQuery<Order> typedQuery = this.em.createQuery(cq);
        typedQuery.setMaxResults(1);

        return !typedQuery.getResultList().isEmpty() ? typedQuery.getSingleResult() : null;
    }

    public List<Order> findOrders(MyPageRequest myPageRequest) {

        CriteriaBuilder cb         = this.em.getCriteriaBuilder();
        CriteriaQuery<Order> cq    = cb.createQuery(Order.class);
        Root<Order> root           = cq.from(Order.class);

        cq = cq.select(root)
                .where(cb.and(this.buildConditions(myPageRequest, cb, root).toArray(new Predicate[0])))
                .distinct(true);

        TypedQuery<Order> typedQuery = this.em.createQuery(cq);
        typedQuery.setFirstResult(myPageRequest.getPage() - 1);
        typedQuery.setMaxResults(myPageRequest.getPageLimit());

        return typedQuery.getResultList();
    }

    private List<Predicate> buildConditions(MyPageRequest myPageRequest, CriteriaBuilder cb, Root<Order> root) {
        List<Predicate> searchConditions    = new ArrayList<>();

        if(myPageRequest.getOrderStartDate() != null && myPageRequest.getOrderEndDate() != null){
            searchConditions.add(cb.between(root.get("orderDate"), myPageRequest.getOrderStartDate(), myPageRequest.getOrderEndDate()));
        }

        if(myPageRequest.getOrderStatus() != null && !myPageRequest.getOrderStatus().isEmpty()){
            searchConditions.add(cb.and(root.get("orderStatus").in(myPageRequest.getOrderStatus())));
        }

        if(myPageRequest.getOrderType() != null && !myPageRequest.getOrderType().isEmpty()){
            searchConditions.add(cb.and(root.get("orderType").in(myPageRequest.getOrderType())));
        }

        if(myPageRequest.getMemberSeq() > 0){
            searchConditions.add(cb.equal(root.get("memberSeq"), myPageRequest.getMemberSeq()));
        }

        if(myPageRequest.getMemberId() != null && !myPageRequest.getMemberId().isEmpty()){
            searchConditions.add(cb.equal(root.get("memberId"), myPageRequest.getMemberId()));
        }

        return  searchConditions;
    }

    private int calculateOffset(int page, int limit) {
        return ((limit * page) - limit);
    }
}
