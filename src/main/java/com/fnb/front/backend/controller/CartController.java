package com.fnb.front.backend.controller;

import com.fnb.front.backend.Service.CartService;
import com.fnb.front.backend.controller.domain.Cart;
import com.fnb.front.backend.controller.domain.request.order.CartInfoResponse;
import com.fnb.front.backend.controller.domain.request.order.CartRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/cart")
    public ResponseEntity<Boolean> addCart(@RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok(this.cartService.create(cartRequest));
    }

    @GetMapping("/cart/{memberId}")
    public ResponseEntity<CartInfoResponse> getCart(@PathVariable String memberId) {
        return ResponseEntity.ok(this.cartService.getInfo(memberId));
    }
}
