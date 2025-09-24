package com.fnb.backend.repository;

import com.fnb.backend.controller.domain.Merchant;
import com.fnb.backend.controller.domain.Order;
import com.fnb.backend.controller.domain.OrderProduct;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
