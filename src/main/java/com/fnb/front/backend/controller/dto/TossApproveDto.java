package com.fnb.front.backend.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@Setter
public class TossApproveDto {
    private String paymentKey;
    private String attemptKey;
}
