package com.fnb.front.backend.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentDto {
    private String approvalId;
    private String transactionId;
    private String cancelId;
    private String status;
    private String productName;
    private int quantity;
    private int totalAmount;
    private int taxFree;
    private int vat;
    private int point;
    private int discount;
    private int greenDeposit;
    private LocalDateTime approvedAt;
    private LocalDateTime createAt;
    private LocalDateTime cancelAt;
}