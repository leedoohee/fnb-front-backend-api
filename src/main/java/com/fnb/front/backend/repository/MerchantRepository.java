package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Merchant;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MerchantRepository {

    @Autowired
    private EntityManager em;

    public Merchant getMerchant(String merchantId) {
        return em.createQuery("SELECT m FROM Merchant m WHERE m.id = : merchantId", Merchant.class)
                .setParameter("merchantId", merchantId)
                .getSingleResult();
    }
}
