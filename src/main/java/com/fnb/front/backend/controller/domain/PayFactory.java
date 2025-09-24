package com.fnb.front.backend.controller.domain;

import com.fnb.front.backend.controller.domain.pay.KakaoPay;
import com.fnb.front.backend.controller.domain.pay.NaverPay;
import com.fnb.front.backend.controller.domain.pay.TossPay;
import com.fnb.front.backend.controller.domain.implement.IPay;

public class PayFactory {
    public static IPay getPay(String payType) {
        if ("K".equals(payType)) {
            return new KakaoPay();
        } else if ("N".equals(payType)) {
            return new NaverPay();
        } else if ("T".equals(payType)) {
            return new TossPay();
        }

        return null; // 또는 기본 전략 반환
    }
}
