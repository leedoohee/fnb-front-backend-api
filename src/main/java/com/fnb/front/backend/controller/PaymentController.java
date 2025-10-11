package com.fnb.front.backend.controller;

import com.fnb.front.backend.Service.PaymentService;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.dto.ApprovePaymentDto;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    //카카오 페이 및 pg 전용
    @PostMapping("/payment/request")
    public ResponseEntity<RequestPaymentResponse> request(@RequestBody RequestPayment requestPayment) {
        return ResponseEntity.ok(this.paymentService.request(requestPayment));
    }

    @PostMapping("/payment/kakao/approve")
    public ResponseEntity<Boolean> approve(@RequestBody ApprovePaymentDto approvePaymentDto) {
        return ResponseEntity.ok(this.paymentService.approveKakaoResult(approvePaymentDto));
    }
}
