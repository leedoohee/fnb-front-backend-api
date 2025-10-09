package com.fnb.front.backend.controller.domain.request.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartRequest {

    private int productId;
    private int basicOptionId;
    private String memberId;
    private int quantity;

    private List<CartItemRequest> cartItemRequests;
}
