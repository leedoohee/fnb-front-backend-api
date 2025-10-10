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

    public Cart findCart(String memberId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Cart> cq = cb.createQuery(Cart.class);

        Root<Cart> cartRoot = cq.from(Cart.class);
        cartRoot.fetch("member", JoinType.INNER);

        // 2-2. Cart -> Product (1:1 관계 가정)
        // Cart에 있는 product 필드를 Fetch Join합니다.
        cartRoot.fetch("product", JoinType.INNER);

        // 2-3. Cart -> CartItem (1:N 관계)
        // Fetch 객체를 얻어 CartItem에서 ProductOption으로 다음 JOIN을 연결합니다.
        Fetch<Cart, CartItem> cartItemFetch = cartRoot.fetch("cartItems", JoinType.INNER);

        // 2-4. CartItem -> ProductOption (N:1 관계)
        // CartItem에 있는 productOption 필드를 Fetch Join합니다.
        cartItemFetch.fetch("productOption", JoinType.INNER);

        // 3. WHERE 조건 및 DISTINCT 설정
        cq.select(cartRoot)
                .where(cb.equal(cartRoot.get("memberId"), memberId))
                .distinct(true); // 1:N 관계 (Cart -> CartItem) 때문에 중복 제거를 위해 필수

        // 4. 쿼리 실행
        TypedQuery<Cart> query = em.createQuery(cq);

        return query.getSingleResult();
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
