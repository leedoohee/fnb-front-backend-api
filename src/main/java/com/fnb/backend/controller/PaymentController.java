package com.fnb.backend.controller;

import com.fnb.backend.Service.PaymentService;
import com.fnb.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.backend.controller.dto.ApprovePaymentDto;
import com.fnb.backend.controller.dto.RequestPaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payment/request")
    public ResponseEntity<RequestPaymentResponse> request(@RequestBody RequestPaymentDto requestPaymentDto) {
        return ResponseEntity.ok(this.paymentService.request(requestPaymentDto));
    }

    @PostMapping("/payment/approve")
    public void approve(@RequestBody ApprovePaymentDto approvePaymentDto) {
        this.paymentService.approve(approvePaymentDto);
    }
}
