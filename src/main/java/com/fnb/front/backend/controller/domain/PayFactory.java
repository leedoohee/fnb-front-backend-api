package com.fnb.front.backend.controller.domain;

import com.fnb.front.backend.controller.domain.pay.KakaoPay;
import com.fnb.front.backend.controller.domain.pay.NaverPay;
import com.fnb.front.backend.controller.domain.pay.TossPay;
import com.fnb.front.backend.controller.domain.implement.IPay;
import com.fnb.front.backend.util.PayType;

public class PayFactory {
    public static IPay getPay(String payType) {
        if (payType.equals(PayType.KAKAO.getValue())) {
            return new KakaoPay();
        } else if (payType.equals(PayType.NAVER.getValue())) {
            return new NaverPay();
        } else if (payType.equals(PayType.TOSS.getValue())) {
            return new TossPay();
        }

        return null; // 또는 기본 전략 반환
    }
}
