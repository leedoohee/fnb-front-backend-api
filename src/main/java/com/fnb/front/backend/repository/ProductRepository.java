package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.AdditionalOption;
import com.fnb.front.backend.controller.domain.Product;
import com.fnb.front.backend.controller.domain.ProductOption;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepository {

    @Autowired
    private EntityManager em;

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

    public List<Product> findByMerchantId(int merchantId) {
        return em.createQuery("SELECT p FROM Product p WHERE p.merchantId = :merchantId", Product.class)
                .setParameter("merchantId", merchantId)
                .getResultList();
    }

    public List<Product> findInIds(String ids) {
        return em.createQuery("SELECT p FROM Product p WHERE p.id in ( :ids )", Product.class)
                .setParameter("ids", ids)
                .getResultList();
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

    public List<AdditionalOption> findAdditionalOptById(String additionalOptionIds) {
        return em.createQuery("SELECT ao FROM AdditionalOption ao WHERE ao.id in ( :additionalOptionIds )", AdditionalOption.class)
                .setParameter("additionalOptionIds", additionalOptionIds)
                .getResultList();
    }

    public List<ProductOption> findOptionsById(int productId) {
        return em.createQuery("SELECT ao FROM AdditionalOption ao WHERE ao.id in ( :productId )", ProductOption.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    public List<AdditionalOption> findAdditionalOptsById(int productId) {
        return em.createQuery("SELECT ao FROM AdditionalOption ao WHERE ao.id in ( :productId )", AdditionalOption.class)
                .setParameter("productId", productId)
                .getResultList();
    }
}
