package com.fnb.front.backend.repository;

import com.fnb.front.backend.controller.domain.Review;
import com.fnb.front.backend.controller.domain.ReviewAttachFile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewRepository {

    private final EntityManager em;

    public ReviewRepository(EntityManager em) {
        this.em = em;
    }

    public List<Review> findReviews(int productId) {

        CriteriaBuilder cb          = em.getCriteriaBuilder();
        CriteriaQuery<Review> cq    = cb.createQuery(Review.class);
        Root<Review> root           = cq.from(Review.class);

        root.fetch("reviewAttachFiles", JoinType.LEFT);

        cq = cq.select(root)
                .where(cb.and(cb.equal(root.get("productId"), productId)))
                .distinct(true);

        TypedQuery<Review> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public List<Review> findReviews(String memberId) {

        CriteriaBuilder cb          = em.getCriteriaBuilder();
        CriteriaQuery<Review> cq    = cb.createQuery(Review.class);
        Root<Review> root           = cq.from(Review.class);

        root.fetch("reviewAttachFiles", JoinType.LEFT);

        cq = cq.select(root)
                .where(cb.and(cb.equal(root.get("memberId"), memberId)))
                .distinct(true);

        TypedQuery<Review> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }

    public List<ReviewAttachFile> findAttachFile(int reviewId) {
        CriteriaBuilder cb          = em.getCriteriaBuilder();
        CriteriaQuery<ReviewAttachFile> cq    = cb.createQuery(ReviewAttachFile.class);
        Root<ReviewAttachFile> root           = cq.from(ReviewAttachFile.class);

        cq = cq.where(cb.and(cb.equal(root.get("reviewId"), reviewId)));
        TypedQuery<ReviewAttachFile> typedQuery = em.createQuery(cq);

        return typedQuery.getResultList();
    }
}
