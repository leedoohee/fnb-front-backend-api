package com.fnb.front.backend.controller.domain.request.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CartInfoResponse {
    private int cartId;
    private int productId;
    private String productName;
    private String description;
    private int minQuantity;
    private int maxQuantity;
    private int quantity;
    private String memberId;
    private String memberName;
    private String address;

    private List<OptionInfoResponse> options;
}
