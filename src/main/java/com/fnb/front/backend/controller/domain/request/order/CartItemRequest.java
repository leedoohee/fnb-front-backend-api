package com.fnb.front.backend.controller.domain.request.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartItemRequest {

    private int cartId;
    private String optionType;
    private int optionGroupId;
    private int optionId;
}
