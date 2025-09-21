package com.fnb.backend.repository;

import com.fnb.backend.controller.domain.Product;
import com.fnb.backend.controller.domain.ProductOption;
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

    public List<Product> findInIds(String ids) {
        return em.createQuery("SELECT p FROM Product p WHERE p.id in ( :ids )", Product.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    public List<ProductOption> findOptionsById(int productId, int optionId) {
        return em.createQuery("SELECT p FROM ProductOption p WHERE p.id in ( :productId )", ProductOption.class)
                .setParameter("productId", productId)
                .getResultList();
    }
}
