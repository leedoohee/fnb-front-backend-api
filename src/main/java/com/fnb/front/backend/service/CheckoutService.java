package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.command.RequestCancelCommand;
import com.fnb.front.backend.controller.domain.command.RequestPaymentCommand;
import com.fnb.front.backend.controller.domain.request.OrderRequest;
import com.fnb.front.backend.controller.domain.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderService orderService;
    private final PaymentApplicationService paymentApplicationService;

    public OrderResponse createOrder(OrderRequest request) {

        OrderResponse response = this.orderService.create(request);

        if (response.getPurchasePrice().compareTo(BigDecimal.ZERO) == 0) {
            //결제 금액 0원이면
            this.paymentApplicationService.handleRequestPayment(RequestPaymentCommand.builder()
                    .orderId(response.getOrderId()).build());
        }

        return response;
    }

    public void cancelOrder(String orderId) {
        this.paymentApplicationService.handleRequestCancel(RequestCancelCommand.builder()
                .orderId(orderId).build());
    }
}