package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.Member;
import com.fnb.front.backend.controller.domain.MemberPointRule;
import com.fnb.front.backend.controller.domain.PointCalculator;
import com.fnb.front.backend.controller.domain.PointFactory;
import com.fnb.front.backend.controller.domain.event.OrderResultEvent;
import com.fnb.front.backend.util.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

@Service
public class PointService {

    @TransactionalEventListener()
    public void handlePointToOrderMember(OrderResultEvent event) {
        Member member = event.getMember();
        //TODO 페이에 따른 추가적립
        int applyPoint        = this.applyPointForOrder(event.getPayType(), member, event.getTotalProductAmount(), event.getPaymentAmount());
        // 포인트 저장
        BigDecimal usePoint   = event.getOrder().getUsePoint();
        //포인트 차감
    }

    private int applyPointForOrder(String payType, Member member, BigDecimal totalProductAmount, BigDecimal paymentAmount) {
        MemberPointRule rule = member.getMemberGrade().getMemberPointRule();
        int point = 0;

        if(CommonUtil.isProductAmountPolicyType(rule.getApplyUnit())){
            if(CommonUtil.isMinAndMaxBetween(rule.getMinApplyAmount().intValue(), rule.getMaxApplyAmount().intValue(), totalProductAmount.intValue())) {
                PointCalculator pointCalculator = new PointCalculator(totalProductAmount,
                        rule.getAddingPointAmount(), PointFactory.getPolicy(rule.getAddingPointType()));

                point += pointCalculator.calculate().intValue();
            }
        }

        if(CommonUtil.isProductAmountPolicyType(rule.getApplyUnit())) {
            if(CommonUtil.isMinAndMaxBetween(rule.getMinApplyAmount().intValue(), rule.getMaxApplyAmount().intValue(), paymentAmount.intValue())) {
                PointCalculator pointCalculator = new PointCalculator(paymentAmount,
                        rule.getAddingPointAmount(), PointFactory.getPolicy(rule.getAddingPointType()));

                point += pointCalculator.calculate().intValue();
            }
        }

        point += this.calculateSpecificPaymentPoint(payType, point);

        return point;
    }

    private int calculateSpecificPaymentPoint(String payType, int point) {
        if(payType == null) return 0;

        //정률, 정액
        //페이별 분기

        return point;
    }
}
