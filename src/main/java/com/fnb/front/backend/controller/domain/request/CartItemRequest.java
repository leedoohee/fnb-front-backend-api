package com.fnb.front.backend.controller.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartItemRequest {

    @NotNull
    private int cartId;

    @NotBlank
    private String optionType;

    @NotNull
    private int optionGroupId;

    @NotNull
    private int optionId;
}
