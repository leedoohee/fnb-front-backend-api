package com.fnb.front.backend.controller;

import com.fnb.front.backend.security.CustomUserDetails;
import com.fnb.front.backend.service.ReviewService;
import com.fnb.front.backend.controller.domain.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product-review/list")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@RequestParam("productId") int productId) {
        return ResponseEntity.ok(this.reviewService.getProductReviews(productId));
    }

    @GetMapping("/my-review/list")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(this.reviewService.getMyReviews(user.getUserId()));
    }

    //TODO 리뷰 등록 및 수정 api 추가
}
