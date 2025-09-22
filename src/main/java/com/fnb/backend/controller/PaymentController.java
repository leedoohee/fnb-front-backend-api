package com.fnb.backend.controller;

import com.fnb.backend.Service.PaymentService;
import com.fnb.backend.controller.domain.response.PaymentResultResponse;
import com.fnb.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.backend.controller.dto.ApprovePaymentDto;
import com.fnb.backend.controller.domain.request.Payment.RequestPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    //카카오 페이 및 pg 전용
    @PostMapping("/payment/request")
    public ResponseEntity<RequestPaymentResponse> request(@RequestBody RequestPayment requestPayment) {
        return ResponseEntity.ok(this.paymentService.request(requestPayment));
    }

    @PostMapping("/payment/kakao/approve")
    public PaymentResultResponse approve(@RequestBody ApprovePaymentDto approvePaymentDto) {
        return this.paymentService.approveKakaoResult(approvePaymentDto);
    }
}
