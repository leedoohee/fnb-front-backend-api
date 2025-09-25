package com.fnb.front.backend.controller;

import com.fnb.front.backend.Service.ReviewService;
import com.fnb.front.backend.controller.domain.response.ReviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/review/{productId}")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable int productId) {
        return ResponseEntity.ok(this.reviewService.getProductReviews(productId));
    }

    @GetMapping("/review/{memberId}")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@PathVariable int memberId) {
        return ResponseEntity.ok(this.reviewService.getMyReviews(memberId));
    }
}
