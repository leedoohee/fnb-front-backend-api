package com.fnb.backend.controller.dto;


import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@Builder
public class CreateOrderDto {

    private int memberId;
    private String merchantId;
    private Long eventId;
    private String orderId;
    private Date orderDate;
    private List<CreateOrderProductDto> orderProducts;
}
