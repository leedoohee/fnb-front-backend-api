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
    private final String orderId;
    private final int paymentId;

    @Builder
    public AfterPaymentCancelCommand(CancelPayDto cancelPayDto, String orderId, int paymentId) {
        this.cancelPayDto = cancelPayDto;
        this.orderId = orderId;
        this.paymentId = paymentId;
    }
}
