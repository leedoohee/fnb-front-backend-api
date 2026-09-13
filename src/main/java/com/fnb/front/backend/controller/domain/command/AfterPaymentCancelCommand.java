package com.fnb.front.backend.controller.domain.command;

import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.Payment;
import com.fnb.front.backend.controller.dto.CancelPayDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AfterPaymentCancelCommand {
    private final CancelPayDto cancelPayDto;
    private final Order order;
    private final Payment payment;

    @Builder
    public AfterPaymentCancelCommand(CancelPayDto cancelPayDto, Order order, Payment payment) {
        this.cancelPayDto = cancelPayDto;
        this.order = order;
        this.payment = payment;
    }
}
