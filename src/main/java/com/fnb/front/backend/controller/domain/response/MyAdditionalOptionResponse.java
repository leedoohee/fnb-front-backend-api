package com.fnb.front.backend.controller.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyAdditionalOptionResponse {
    private int quantity;
    private String optionName;
    private String optionId;
    private int price;
}
