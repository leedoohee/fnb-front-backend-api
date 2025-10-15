package com.fnb.front.backend.controller;

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

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(this.orderService.create(orderRequest));
    }

    @PutMapping("cancel-order/{orderId}")
    public ResponseEntity<Boolean> cancel(@PathVariable String orderId) {
        return ResponseEntity.ok(this.orderService.cancel(orderId));
    }
}
