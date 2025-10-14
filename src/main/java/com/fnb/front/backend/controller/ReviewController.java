package com.fnb.front.backend.controller;

import com.fnb.front.backend.service.ReviewService;
import com.fnb.front.backend.controller.domain.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/review/{productId}")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable int productId) {
        return ResponseEntity.ok(this.reviewService.getProductReviews(productId));
    }

    @GetMapping("/review/{memberId}")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@PathVariable String memberId) {
        return ResponseEntity.ok(this.reviewService.getMyReviews(memberId));
    }

    //TODO 리뷰 등록 및 수정 api 추가
}
