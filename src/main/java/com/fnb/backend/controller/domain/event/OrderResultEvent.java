package com.fnb.backend.controller.domain.event;

import com.fnb.backend.controller.domain.Member;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderResultEvent {
    private Member member;
    private BigDecimal paymentAmount;
    private BigDecimal totalProductAmount;
}
