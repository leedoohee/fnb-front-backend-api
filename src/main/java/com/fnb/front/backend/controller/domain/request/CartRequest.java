package com.fnb.front.backend.controller.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartRequest {

    @NotNull
    private int productId;

    @NotBlank
    private String memberId;

    @NotNull
    private int quantity;

    @NotEmpty
    private List<CartItemRequest> cartItemRequests;
}
