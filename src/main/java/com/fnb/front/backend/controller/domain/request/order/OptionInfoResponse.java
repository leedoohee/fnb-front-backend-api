package com.fnb.front.backend.controller.domain.request.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OptionInfoResponse {
    private int optionGroupId;
    private int optionId;
    private String optionType;
    private String optionName;
    private int price;
}
