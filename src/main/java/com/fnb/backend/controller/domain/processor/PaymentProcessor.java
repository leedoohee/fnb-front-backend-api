package com.fnb.backend.controller.domain.processor;

import com.fnb.backend.controller.domain.event.PaymentCompletedEvent;
import com.fnb.backend.controller.domain.implement.IPay;
import com.fnb.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.backend.controller.dto.ApprovePaymentDto;
import com.fnb.backend.controller.dto.RequestPaymentDto;
import org.springframework.context.ApplicationEventPublisher;

public class PaymentProcessor {

    private final ApplicationEventPublisher eventPublisher ;

    private IPay IPay;

    public PaymentProcessor(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setPay(IPay IPay) {
        this.IPay = IPay;
    }

    public RequestPaymentResponse request(RequestPaymentDto requestPaymentDto) {
        return this.IPay.request(requestPaymentDto);
    }

    public ApprovePaymentResponse approve(ApprovePaymentDto approvePaymentDto) {
        return this.processAfterApproval(this.IPay.approve(approvePaymentDto));
    }

    private ApprovePaymentResponse processAfterApproval(ApprovePaymentResponse buildRequest) {
        return buildRequest;
    }
}
