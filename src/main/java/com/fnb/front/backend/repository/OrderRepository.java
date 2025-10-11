package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.OrderOption;
import com.fnb.front.backend.controller.domain.OrderProduct;
import com.fnb.front.backend.controller.domain.request.MyPageRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderRepository {

    private final EntityManager em;

    public OrderRepository(EntityManager em) {
        this.em = em;
    }

    public void insertOrder(Order order) {
        em.persist(order);
    }

    public void insertOrderProducts(List<OrderProduct> orderProducts) {
        em.persist(orderProducts);
    }

    public List<OrderProduct> findOrderProducts(String orderId) {
        CriteriaBuilder cb               = em.getCriteriaBuilder();
        CriteriaQuery<OrderProduct> cq   = cb.createQuery(OrderProduct.class);
        Root<OrderProduct> root          = cq.from(OrderProduct.class);

        cq = cq.where(cb.and(cb.equal(root.get("orderId"), orderId)));
        TypedQuery<OrderProduct> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public Long findTotalOrderCount(MyPageRequest orderRequest) {
        CriteriaBuilder cb          = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq      = cb.createQuery(Long.class);
        Root<Order> root            = cq.from(Order.class);

        cq = cq.where(cb.and(this.buildConditions(orderRequest, cb, root).toArray(new Predicate[0])));
        cq = cq.select((cb.count(root)));

        return  em.createQuery(cq).getSingleResult();
    }

    public Order findOrder(String orderId) {
        CriteriaBuilder cb        = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq   = cb.createQuery(Order.class);
        Root<Order> root          = cq.from(Order.class);

        root.fetch("member", JoinType.INNER);
        Fetch<Order, OrderProduct> orderProductFetch = root.fetch("orderProduct", JoinType.INNER);
        orderProductFetch.fetch("product", JoinType.INNER);

        cq = cq.select(root)
                .where(cb.and(cb.equal(root.get("orderId"), orderId)))
                .distinct(true);

        TypedQuery<Order> typedQuery = em.createQuery(cq);

        return typedQuery.getSingleResult();
    }

    public List<Order> findOrders(MyPageRequest myPageRequest) {

        CriteriaBuilder cb         = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq    = cb.createQuery(Order.class);
        Root<Order> root           = cq.from(Order.class);

        Fetch<Order, OrderProduct> orderProductFetch = root.fetch("orderProduct", JoinType.INNER);

        orderProductFetch.fetch("orderOption", JoinType.INNER);

        cq = cq.select(root)
                .where(cb.and(this.buildConditions(myPageRequest, cb, root).toArray(new Predicate[0])))
                .distinct(true);

        TypedQuery<Order> typedQuery = em.createQuery(cq);
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
