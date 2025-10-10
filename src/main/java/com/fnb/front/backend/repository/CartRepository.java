package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Cart;
import com.fnb.front.backend.controller.domain.CartItem;
import com.fnb.front.backend.controller.domain.Coupon;
import com.fnb.front.backend.controller.domain.MemberCoupon;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CartRepository {

    private final EntityManager em;

    public CartRepository(EntityManager em) {
        this.em = em;
    }

    public int insertCart(Cart cart) {
        em.persist(cart);

        return cart.getId();
    }

    public void insertCartItem(CartItem cartItem) {
        em.persist(cartItem);
    }

    public Cart findCart(String memberId) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Cart> cq = cb.createQuery(Cart.class);
        Root<Cart> root = cq.from(Cart.class);

        cq = cq.where(cb.and(cb.equal(root.get("memberId"), memberId)));

        TypedQuery<Cart> typedQuery = em.createQuery(cq);

        return typedQuery.getSingleResult();
    }

    public List<CartItem> findCartItems(int cartId) {
        CriteriaBuilder cb          = this.em.getCriteriaBuilder();
        CriteriaQuery<CartItem> cq  = cb.createQuery(CartItem.class);
        Root<CartItem> root         = cq.from(CartItem.class);

        cq = cq.where(cb.and(cb.equal(root.get("cartId"), cartId)));

        TypedQuery<CartItem> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public List<CartItem> findCartItems(String memberId) {
        CriteriaBuilder cb          = this.em.getCriteriaBuilder();
        CriteriaQuery<CartItem> cq  = cb.createQuery(CartItem.class);
        Root<CartItem> root         = cq.from(CartItem.class);

        root.fetch("Cart", JoinType.INNER);
        root.fetch("productOption", JoinType.INNER);
        root.fetch("member", JoinType.INNER);
        root.fetch("product", JoinType.INNER);

        cq = cq.where(cb.and(cb.equal(root.get("memberId"), memberId)));

        TypedQuery<CartItem> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public void deleteCart(int cartId) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        CriteriaDelete<Cart> delete = cb.createCriteriaDelete(Cart.class);
        Root<Cart> root = delete.from(Cart.class);

        delete = delete.where(cb.and(cb.equal(root.get("id"), cartId)));

        this.em.createQuery(delete).executeUpdate();
    }

    public void deleteCartItem(int cartId) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        CriteriaDelete<CartItem> delete = cb.createCriteriaDelete(CartItem.class);
        Root<CartItem> root = delete.from(CartItem.class);

        delete = delete.where(cb.and(cb.equal(root.get("cartId"), cartId)));

        this.em.createQuery(delete).executeUpdate();
    }
}
