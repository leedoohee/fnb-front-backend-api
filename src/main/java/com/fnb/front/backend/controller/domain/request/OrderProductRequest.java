package com.fnb.front.backend.controller.domain.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderProductRequest {

    @NotNull
    private int productId;

    @NotEmpty
    private List<Integer> productOptionIds;

    @NotNull
    private int quantity;
}
