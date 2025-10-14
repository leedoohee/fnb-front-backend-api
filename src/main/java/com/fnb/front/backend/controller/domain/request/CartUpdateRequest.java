package com.fnb.front.backend.controller.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartUpdateRequest {

    @NotNull
    private int cartId;

    @NotNull
    private int productId;

    @NotNull
    private int quantity;
}
