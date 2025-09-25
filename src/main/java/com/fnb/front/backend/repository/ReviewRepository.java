package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Coupon;
import com.fnb.front.backend.controller.domain.Review;
import com.fnb.front.backend.controller.domain.ReviewAttachFile;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewRepository {

    @Autowired
    private EntityManager em;

    public List<Review> findReviews(int productId) {
        return em.createQuery("SELECT r FROM Review r WHERE r.productId = :productId", Review.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    public List<Review> findReviewsByMemberId(int memberId) {
        return em.createQuery("SELECT r FROM Review r WHERE r.registerId = :memberId", Review.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<ReviewAttachFile> findAttachFile(int reviewId) {
        return em.createQuery("SELECT raf FROM ReviewAttachFile raf WHERE raf.reviewId = :reviewId", ReviewAttachFile.class)
                .setParameter("reviewId", reviewId)
                .getResultList();
    }
}
