package com.fnb.front.backend.controller.dto;


import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Builder
public class CreateOrderDto {

    private String memberId;
    private String memberName;
    private String orderId;
    private LocalDateTime orderDate;
    private BigDecimal orderAmount;
    private BigDecimal discountAmount;
    private BigDecimal shippingAmount;
    private int couponAmount;
    private String usePoint;
    private String orderType;
    private List<CreateOrderProductDto> orderProducts;
}
