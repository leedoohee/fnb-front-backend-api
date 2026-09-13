package com.fnb.front.backend.controller;

import com.fnb.front.backend.service.CheckoutService;
import com.fnb.front.backend.service.OrderService;
import com.fnb.front.backend.controller.domain.response.OrderResponse;
import com.fnb.front.backend.controller.domain.request.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final CheckoutService checkoutService;

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(this.checkoutService.createOrder(orderRequest));
    }

    @PutMapping("/cancel-order/{orderId}")
    public ResponseEntity<Boolean> cancel(@PathVariable String orderId) {
        this.checkoutService.cancelOrder(orderId);
        return ResponseEntity.ok(true);
    }
}
