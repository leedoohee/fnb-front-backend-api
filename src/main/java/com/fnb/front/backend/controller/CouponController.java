package com.fnb.front.backend.controller;

import com.fnb.front.backend.Service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/coupon/list")
    public void getCoupons() {
        
    }

    @PostMapping("/coupon/validate-apply/{memberId}/{couponId}")
    public ResponseEntity<Boolean> validateMemberCoupon(@PathVariable String memberId, @PathVariable int couponId, @RequestParam int productId) {
        return ResponseEntity.ok(this.couponService.applyCouponToProduct(memberId, couponId, productId));
    }

    @PostMapping("/coupon/{memberId}/{couponId}")
    public ResponseEntity<Boolean> createMemberCoupon(@PathVariable String memberId, @PathVariable int couponId) {
        return ResponseEntity.ok(couponService.createMemberCoupon(memberId, couponId));
    }
}
