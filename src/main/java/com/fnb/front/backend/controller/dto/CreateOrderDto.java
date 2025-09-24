package com.fnb.front.backend.controller.dto;


import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Builder
public class CreateOrderDto {

    private int memberId;
    private String merchantId;
    private String orderId;
    private Date orderDate;
    private BigDecimal orderAmount;
    private BigDecimal discountAmount;
    private BigDecimal shippingAmount;
    private BigDecimal couponAmount;
    private String usePoint;
    private String orderType;
    private List<CreateOrderProductDto> orderProducts;
}
