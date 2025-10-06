package com.fnb.front.backend.controller;

import com.fnb.front.backend.Service.ProductService;
import com.fnb.front.backend.controller.domain.response.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/list")
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(this.productService.getProducts());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> getInfo(@PathVariable int productId) {
        return ResponseEntity.ok(this.productService.getInfo(productId));
    }
}
