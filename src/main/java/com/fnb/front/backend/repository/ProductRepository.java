package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository {

    private final EntityManager em;

    public ProductRepository(EntityManager em) {
        this.em = em;
    }

    public Product findProduct(int productId) {
        CriteriaBuilder cb           = this.em.getCriteriaBuilder();
        CriteriaQuery<Product> cq    = cb.createQuery(Product.class);
        Root<Product> root           = cq.from(Product.class);

        root.fetch("productOption", JoinType.LEFT);

        cq = cq.select(root)
                .where(cb.and(cb.equal(root.get("id"), productId)))
                .distinct(true);

        TypedQuery<Product> typedQuery = this.em.createQuery(cq);
        typedQuery.setMaxResults(1);

        return !typedQuery.getResultList().isEmpty() ? typedQuery.getResultList().get(0) : null;
    }

    public List<Product> findProducts(String status) {
        CriteriaBuilder cb           = this.em.getCriteriaBuilder();
        CriteriaQuery<Product> cq    = cb.createQuery(Product.class);
        Root<Product> root           = cq.from(Product.class);

        root.fetch("productAttachFile", JoinType.INNER);

        cq = cq.select(root)
                .where(cb.and(cb.equal(root.get("status"), status)))
                .distinct(true);

        TypedQuery<Product> typedQuery = this.em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public List<Product> findProducts(List<Integer> productIds) {
        List<Predicate> searchConditions    = new ArrayList<>();
        CriteriaBuilder cb                  = this.em.getCriteriaBuilder();
        CriteriaQuery<Product> cq           = cb.createQuery(Product.class);
        Root<Product> root                  = cq.from(Product.class);

        searchConditions.add(cb.equal(root.get("productId"), productIds));

        cq = cq.select(root)
                .where(cb.and(searchConditions.toArray(new Predicate[0])))
                .distinct(true);

        TypedQuery<Product> typedQuery = this.em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public List<Product> findProducts(List<Integer> productIds, List<Integer> optionIds) {
        List<Predicate> searchConditions    = new ArrayList<>();
        CriteriaBuilder cb                  = this.em.getCriteriaBuilder();
        CriteriaQuery<Product> cq           = cb.createQuery(Product.class);
        Root<Product> root                  = cq.from(Product.class);

        searchConditions.add(root.join("productOption", JoinType.INNER).get("orderOptionId").in(optionIds));
        //TODO 옵션의 상태값 조건 추가
        searchConditions.add(cb.equal(root.get("productId"), productIds));

        cq = cq.select(root)
                .where(cb.and(searchConditions.toArray(new Predicate[0])))
                .distinct(true);

        TypedQuery<Product> typedQuery = this.em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public void updateMinusQuantity(int productId, int quantity) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        CriteriaUpdate<Product> update = cb.createCriteriaUpdate(Product.class);
        Root<Product> root = update.from(Product.class);

        Expression<Integer> currentQuantity = root.get("quantity");
        Expression<Integer> newQuantity     = cb.diff(currentQuantity, quantity);

        update.set("quantity", newQuantity);
        update.where(cb.and(cb.equal(root.get("productId"), productId)));

        this.em.createQuery(update).executeUpdate();
    }

    public void updatePlusQuantity(int productId, int quantity) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        CriteriaUpdate<Product> update = cb.createCriteriaUpdate(Product.class);
        Root<Product> root = update.from(Product.class);

        Expression<Integer> currentQuantity = root.get("quantity");
        Expression<Integer> newQuantity     = cb.mod(currentQuantity, quantity);

        update.set("quantity", newQuantity);
        update.where(cb.and(cb.equal(root.get("productId"), productId)));

        this.em.createQuery(update).executeUpdate();
    }
}
