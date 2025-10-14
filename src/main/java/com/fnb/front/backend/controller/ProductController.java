package com.fnb.front.backend.controller;

import com.fnb.front.backend.service.ProductService;
import com.fnb.front.backend.controller.domain.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product/list")
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(this.productService.getProducts());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> getInfo(@PathVariable int productId) {
        return ResponseEntity.ok(this.productService.getInfo(productId));
    }

    @GetMapping("/product/validate/{productId}")
    public ResponseEntity<Boolean> validate(@PathVariable int productId, @RequestParam("quantity") int quantity) {
        return ResponseEntity.ok(this.productService.validate(productId, quantity));
    }
}
