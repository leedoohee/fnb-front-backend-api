package com.fnb.front.backend.controller;

import com.fnb.front.backend.security.CustomUserDetails;
import com.fnb.front.backend.service.CouponService;
import com.fnb.front.backend.controller.domain.response.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/coupon/list")
    public ResponseEntity<List<CouponResponse>> getCoupons() {
        return ResponseEntity.ok(this.couponService.getCoupons());
    }

    @PostMapping("/coupon/valid-apply/{couponId}")
    public ResponseEntity<Boolean> validateMemberCoupon(@AuthenticationPrincipal CustomUserDetails user, @PathVariable int couponId, @RequestParam int productId) {
        return ResponseEntity.ok(this.couponService.applyCouponToProduct(user.getUserId(), couponId, productId));
    }

    @PostMapping("/coupon/{couponId}")
    public ResponseEntity<Boolean> createMemberCoupon(@AuthenticationPrincipal CustomUserDetails user, @PathVariable int couponId) {
        return ResponseEntity.ok(couponService.createMemberCoupon(user.getUserId(), couponId));
    }
}
