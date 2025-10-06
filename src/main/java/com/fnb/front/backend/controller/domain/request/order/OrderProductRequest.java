package com.fnb.front.backend.controller.domain.request.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderProductRequest {
    private int productId;
    private List<Integer> productOptionId;
    private int quantity;
}
