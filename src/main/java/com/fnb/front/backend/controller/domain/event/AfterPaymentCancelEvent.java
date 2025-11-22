package com.fnb.front.backend.controller.domain.event;

import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.Payment;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.dto.CancelPayDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AfterPaymentCancelEvent extends ApplicationEvent {
    private final CancelPayDto cancelPayDto;
    private final Order order;
    private final Payment payment;

    @Builder
    public AfterPaymentCancelEvent(Object source, CancelPayDto cancelPayDto, Order order, Payment payment) {
        super(source);
        this.cancelPayDto = cancelPayDto;
        this.order = order;
        this.payment = payment;
    }
}
