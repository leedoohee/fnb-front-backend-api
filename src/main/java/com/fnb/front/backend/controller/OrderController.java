package com.fnb.front.backend.controller;

import com.fnb.front.backend.security.CustomUserDetails;
import com.fnb.front.backend.service.CheckoutService;
import com.fnb.front.backend.controller.domain.response.OrderResponse;
import com.fnb.front.backend.controller.domain.request.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final CheckoutService checkoutService;

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest orderRequest, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(this.checkoutService.createOrder(orderRequest, user.getUserId()));
    }

    @PutMapping("/cancel-order/{orderId}")
    public ResponseEntity<Boolean> cancel(@PathVariable String orderId, @AuthenticationPrincipal CustomUserDetails user) {
        this.checkoutService.cancelOrder(orderId, user.getUserId());
        return ResponseEntity.ok(true);
    }
}
