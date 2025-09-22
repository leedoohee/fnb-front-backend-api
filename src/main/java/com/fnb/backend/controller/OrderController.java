package com.fnb.backend.controller;

import com.fnb.backend.Service.OrderService;
import com.fnb.backend.controller.request.order.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order")
    public String create(@RequestBody OrderRequest orderRequest) {
        this.orderService.process(orderRequest);

        return "success";
    }
}
