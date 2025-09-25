package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.Review;
import com.fnb.front.backend.controller.domain.response.ReviewResponse;
import com.fnb.front.backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<ReviewResponse> getMyReviews(int memberId) {
        List<Review> myReviews = this.reviewRepository.findReviewsByMemberId(memberId);
        return this.buildReviewResponses(myReviews);
    }

    public List<ReviewResponse> getProductReviews(int productId) {
        List<Review> productReviews = this.reviewRepository.findReviews(productId);
        return this.buildReviewResponses(productReviews);
    }

    private List<ReviewResponse> buildReviewResponses(List<Review> reviews) {
        List<ReviewResponse> reviewResponses = new ArrayList<>();

        for (Review review : reviews) {
            reviewResponses.add(ReviewResponse.builder()
                    .id(review.getId())
                    .content(review.getContent())
                    .registDate(review.getRegistDate())
                    .productId(review.getProductId())
                    .registerId(review.getRegisterId())
                    .attachFiles(review.getAttachFiles()).build());
        }

        return reviewResponses;
    }
}
