package com.fnb.backend.controller.domain.response;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPaymentResponse {
    private String status;
    private String redirectUrl;
    private String transactionId;
}
