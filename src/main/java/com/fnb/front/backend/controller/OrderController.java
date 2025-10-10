package com.fnb.front.backend.controller;

import com.fnb.front.backend.Service.OrderService;
import com.fnb.front.backend.controller.domain.response.OrderResponse;
import com.fnb.front.backend.controller.domain.request.order.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest orderRequest) throws Exception {
        return ResponseEntity.ok(this.orderService.create(orderRequest));
    }
}
