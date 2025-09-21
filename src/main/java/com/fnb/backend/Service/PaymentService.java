package com.fnb.backend.Service;

import com.fnb.backend.controller.domain.PayFactory;
import com.fnb.backend.controller.domain.orderEvent.EnrollPaymentEvent;
import com.fnb.backend.controller.domain.processor.PaymentProcessor;
import com.fnb.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.backend.controller.dto.ApprovePaymentDto;
import com.fnb.backend.controller.dto.RequestPaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {

    @Autowired
    private PaymentProcessor paymentProcessor;

    public RequestPaymentResponse request(RequestPaymentDto requestPaymentDto) {
        return paymentProcessor.request(requestPaymentDto);
    }

    public RequestPaymentResponse approve(ApprovePaymentDto approvePaymentDto) {
        return paymentProcessor.approve(approvePaymentDto);
    }
}
