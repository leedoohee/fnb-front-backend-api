package com.fnb.front.backend.controller.domain.response;

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
