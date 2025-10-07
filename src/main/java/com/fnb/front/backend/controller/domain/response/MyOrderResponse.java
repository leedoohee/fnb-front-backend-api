package com.fnb.front.backend.controller.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class MyOrderResponse {

    private String orderId;
    private String memberName;
    private BigDecimal totalAmount;
    private List<MyOrderProductResponse> orderProducts;
}
