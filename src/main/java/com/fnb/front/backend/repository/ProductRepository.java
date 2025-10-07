package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Product;
import com.fnb.front.backend.controller.domain.ProductOption;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepository {

    private final EntityManager em;

    public ProductRepository(EntityManager em) {
        this.em = em;
    }

    public Product findProduct(int productId) {
        CriteriaBuilder cb           = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq    = cb.createQuery(Product.class);
        Root<Product> root           = cq.from(Product.class);

        cq = cq.where(cb.and(cb.equal(root.get("id"), productId)));
        TypedQuery<Product> typedQuery = em.createQuery(cq);

        return typedQuery.getSingleResult();
    }

    public List<Product> findProducts() {
        CriteriaBuilder cb           = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq    = cb.createQuery(Product.class);
        Root<Product> root           = cq.from(Product.class);

        cq.select(root);
        
        TypedQuery<Product> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public List<Product> findProducts(List<Integer> productIds) {
        CriteriaBuilder cb           = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq    = cb.createQuery(Product.class);
        Root<Product> root           = cq.from(Product.class);

        cq = cq.where(cb.and(root.get("id").in(productIds)));

        TypedQuery<Product> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public List<ProductOption> findOptions(int productId) {

        CriteriaBuilder cb                 = em.getCriteriaBuilder();
        CriteriaQuery<ProductOption> cq    = cb.createQuery(ProductOption.class);
        Root<ProductOption> root           = cq.from(ProductOption.class);

        cq = cq.where(cb.and(cb.equal(root.get("productId"), productId)));
        TypedQuery<ProductOption> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public List<ProductOption> findOptions(List<Integer> productOptionIds) {

        CriteriaBuilder cb                 = em.getCriteriaBuilder();
        CriteriaQuery<ProductOption> cq    = cb.createQuery(ProductOption.class);
        Root<ProductOption> root           = cq.from(ProductOption.class);

        cq = cq.where(cb.and(root.get("productId").in(productOptionIds)));
        TypedQuery<ProductOption> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }
}
