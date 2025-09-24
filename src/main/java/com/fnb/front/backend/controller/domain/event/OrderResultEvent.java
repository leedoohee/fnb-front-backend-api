package com.fnb.front.backend.controller.domain.event;

import com.fnb.front.backend.controller.domain.Member;
import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.OrderProduct;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResultEvent {
    String payType;
    private Member member;
    private Order order;
    private List<OrderProduct> orderProducts;
    private BigDecimal paymentAmount;
    private BigDecimal totalProductAmount;
}
