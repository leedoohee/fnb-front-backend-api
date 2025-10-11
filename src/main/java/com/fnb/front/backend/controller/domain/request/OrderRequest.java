package com.fnb.front.backend.controller.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderRequest {

    @NotBlank
    private String memberId;

    @NotNull
    private int orderType;

    @NotNull
    private BigDecimal point;

    @NotEmpty
    private List<OrderProductRequest> orderProductRequests;

    private List<OrderCouponRequest> orderCouponRequests;
}
