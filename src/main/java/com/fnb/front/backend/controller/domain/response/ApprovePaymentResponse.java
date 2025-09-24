package com.fnb.front.backend.controller.domain.response;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovePaymentResponse {
    private String transactionId;   // 결제사 고유 거래 ID (카카오페이의 tid, 네이버페이/토스페이의 paymentKey)
    private String approvalId;      // 결제 승인 번호 (카카오페이의 aid)
    private String paymentMethod;   // 결제 수단 (카드, 계좌이체 등)
    private LocalDateTime approvedAt; // 결제 승인 시간
    private BigDecimal totalAmount; // 총 결제 금액
    private String gatewayType;     // 결제사 구분 (KAKAO, NAVER, TOSS)
    private String orderId;         // 가맹점 주문 ID
}