package com.fnb.front.backend.controller;

import com.fnb.front.backend.Service.CartService;
import com.fnb.front.backend.controller.domain.request.CartUpdateRequest;
import com.fnb.front.backend.controller.domain.response.CartInfoResponse;
import com.fnb.front.backend.controller.domain.request.CartRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/cart")
    public ResponseEntity<Boolean> addCart(@RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok(this.cartService.create(cartRequest));
    }

    @GetMapping("/cart/{memberId}")
    public ResponseEntity<List<CartInfoResponse>> getCart(@PathVariable String memberId) {
        return ResponseEntity.ok(this.cartService.getInfo(memberId));
    }

    @DeleteMapping("/cart/{cartId}")
    public ResponseEntity<Boolean> deleteCart(@PathVariable int cartId) {
        return ResponseEntity.ok(this.cartService.delete(cartId));
    }

    @PutMapping("/cart")
    public ResponseEntity<Boolean> updateCart(@RequestBody CartUpdateRequest cartUpdateRequest) {
        return ResponseEntity.ok(this.cartService.update(cartUpdateRequest));
    }
}
