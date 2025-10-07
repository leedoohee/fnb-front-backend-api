package com.fnb.front.backend.controller.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MyOrderProductResponse {
    private int orderProductId;
    private String productName;
    private int quantity;
    private int price;
    private String basicOptionId;
    private String basicOptionName;
    private List<MyAdditionalOptionResponse> additionalOptions;
}
