package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.AdditionalOption;
import com.fnb.front.backend.controller.domain.Product;
import com.fnb.front.backend.controller.domain.ProductOption;
import jakarta.persistence.EntityManager;
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

    public Product find(int id) {
        return em.createQuery("SELECT p FROM Product p WHERE p.id = :id", Product.class)
                .setParameter("id", id)
                .getSingleResult();
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

    public ProductOption findOptionById(int optionId) {
        return em.createQuery("SELECT p FROM ProductOption p WHERE p.id = :optionId", ProductOption.class)
                .setParameter("optionId", optionId)
                .getSingleResult();
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
