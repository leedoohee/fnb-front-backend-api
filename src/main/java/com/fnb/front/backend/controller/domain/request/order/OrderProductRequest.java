package com.fnb.front.backend.controller.domain.request.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProductRequest {
    private int productId;
    private int productOptionId;
    private int[] additionalOptionIds;
    private int quantity;
}
