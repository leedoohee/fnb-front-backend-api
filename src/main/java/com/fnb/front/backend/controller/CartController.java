package com.fnb.front.backend.controller;

import com.fnb.front.backend.service.CartService;
import com.fnb.front.backend.controller.domain.request.CartUpdateRequest;
import com.fnb.front.backend.controller.domain.response.CartInfoResponse;
import com.fnb.front.backend.controller.domain.request.CartRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fnb.front.backend.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/cart")
    public ResponseEntity<Boolean> addCart(@RequestBody CartRequest cartRequest, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(this.cartService.create(cartRequest, user.getUserId()));
    }

    @GetMapping("/cart")
    public ResponseEntity<List<CartInfoResponse>> getCart(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(this.cartService.getInfo(user.getUserId()));
    }

    @DeleteMapping("/cart/{cartId}")
    public ResponseEntity<Boolean> deleteCart(@PathVariable int cartId, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(this.cartService.delete(cartId, user.getUserId()));
    }

    @PutMapping("/cart")
    public ResponseEntity<Boolean> updateCart(@RequestBody CartUpdateRequest cartUpdateRequest, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(this.cartService.update(cartUpdateRequest, user.getUserId()));
    }
}
