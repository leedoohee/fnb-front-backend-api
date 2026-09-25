package com.fnb.front.backend.controller;

import com.fnb.front.backend.security.CustomUserDetails;
import com.fnb.front.backend.service.PaymentApplicationService;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    //카카오 페이 및 pg 전용
    @PostMapping("/payment/request")
    public ResponseEntity<RequestPaymentResponse> request(@RequestBody RequestPayment requestPayment, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(this.paymentApplicationService.request(requestPayment, user.getUserId()));
    }

    @GetMapping("/payment/kakao/approve/{attemptKey}")
    public ResponseEntity<Boolean> approveKakao(@RequestParam("pg_token") String pgToken, @PathVariable String attemptKey) {
        this.paymentApplicationService.approveKakaoResult(pgToken, attemptKey);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/payment/kakao/fail/{attemptKey}")
    public ResponseEntity<Void> failKakaoPayment(@PathVariable String attemptKey) {
        paymentApplicationService.failKakaoResult(attemptKey);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/payment/fail")) //프론트 url로 리다이렉트
                .build();
    }
}
