package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Cart;
import com.fnb.front.backend.controller.domain.CartItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
public class CartRepository {

    private final EntityManager em;

    public CartRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public int insertCart(Cart cart) {
        this.em.persist(cart);
        return cart.getCartId();
    }

    @Transactional
    public void insertCartItem(CartItem cartItem) {
        this.em.persist(cartItem);
    }

    public List<Cart> findCarts(String memberId) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Cart> cq = cb.createQuery(Cart.class);
        Root<Cart> root = cq.from(Cart.class);

        root.fetch("member", JoinType.INNER);
        root.fetch("product", JoinType.INNER);
        Fetch<Cart, CartItem> cartItemFetch = root.fetch("cartItems", JoinType.INNER);
        cartItemFetch.fetch("productOption", JoinType.INNER);

        cq = cq.select(root)
                .where(cb.equal(root.get("memberId"), memberId))
                .distinct(true);

        TypedQuery<Cart> query = em.createQuery(cq);

        return query.getResultList();
    }

    public Cart findCart(int cartId) {
        CriteriaBuilder cb      = this.em.getCriteriaBuilder();
        CriteriaQuery<Cart> cq  = cb.createQuery(Cart.class);
        Root<Cart> root         = cq.from(Cart.class);

        cq = cq.select(root).where(cb.equal(root.get("cartId"), cartId));

        TypedQuery<Cart> typedQuery = this.em.createQuery(cq);
        typedQuery.setMaxResults(1);

        return !typedQuery.getResultList().isEmpty() ? typedQuery.getSingleResult() : null;
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

    public void updateCart(int cartId, int quantity) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        CriteriaUpdate<Cart> update = cb.createCriteriaUpdate(Cart.class);
        Root<Cart> root = update.from(Cart.class);

        update.set(root.get("quantity"), quantity);
        update = update.where(cb.and(cb.equal(root.get("cartId"), cartId)));

        this.em.createQuery(update).executeUpdate();
    }
}
