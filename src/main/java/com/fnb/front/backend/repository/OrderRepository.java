package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.OrderProduct;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class OrderRepository {

    @Autowired
    private EntityManager em;

    public void insertOrder(Order order) {
        em.persist(order);
    }

    public void insertOrderProducts(List<OrderProduct> orderProducts) {
        em.persist(orderProducts);
    }

    public List<OrderProduct> getOrderProducts(String orderId) {
        return em.createQuery("SELECT op FROM OrderProduct op WHERE op.orderId = : orderId", OrderProduct.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public Order getOrder(String orderId) {
        return em.createQuery("SELECT o FROM Order o WHERE o.id = : orderId", Order.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
    }

    public List<Order> getOrders(int memberId, String startDate, String endDate,  int page, int pageLimit) {
        return  em.createQuery("SELECT o FROM Order o WHERE o.memberId = :memberId and o.orderDate BETWEEN :startDate and :endDate ORDER BY o.id DESC", Order.class)
                .setParameter("memberId", memberId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setFirstResult(this.calculateOffset(page, pageLimit))
                .setMaxResults(pageLimit)
                .getResultList();
    }

    private int calculateOffset(int page, int limit) {
        return ((limit * page) - limit);
    }
}
