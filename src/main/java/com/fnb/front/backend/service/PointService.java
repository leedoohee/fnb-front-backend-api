package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.repository.MemberRepository;
import com.fnb.front.backend.repository.PointRepository;
import com.fnb.front.backend.util.CommonUtil;
import com.fnb.front.backend.util.PointType;
import com.fnb.front.backend.util.Used;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    private final MemberRepository memberRepository;

    public boolean givePoint(Order order, Member member, BigDecimal totalProductAmount, BigDecimal paymentAmount) {
        //TODO 페이에 따른 추가적립

        int usePoint = order.getUsePoint().intValue();

        if (!member.isUsablePoint(usePoint)) {
            return false;
        }

        int applyPoint = this.applyGradePointForOrder(member, totalProductAmount, paymentAmount);

        MemberPoint minusPoint = MemberPoint.builder()
                .pointType(PointType.MINUS.getValue()) // 차감
                .orderId(order.getOrderId())
                .memberId(member.getMemberId())
                .amount(usePoint)
                .isUsed(Used.USED.getValue())
                .build();

        this.pointRepository.insertMemberPoint(minusPoint);

        MemberPoint plusPoint = MemberPoint.builder()
                .pointType(PointType.PLUS.getValue()) // 적립
                .orderId(order.getOrderId())
                .memberId(member.getMemberId())
                .amount(applyPoint)
                .isUsed(Used.NOTUSED.getValue())
                .build();

        this.pointRepository.insertMemberPoint(plusPoint);

        return true;
    }

    public void returnPoint(String orderId) {
        MemberPoint memberPoint = this.memberRepository.findMemberPoint(orderId);
        this.memberRepository.updateMinusPoint(memberPoint.getMemberId(), memberPoint.getAmount());
        this.pointRepository.deleteMemberPoint(orderId);
    }

    private int applyGradePointForOrder(Member member, BigDecimal totalProductAmount, BigDecimal paymentAmount) {
        MemberPointRule rule = member.getMemberGrade().getMemberPointRule();
        int point = 0;

        if(CommonUtil.isProductAmountPolicyType(rule.getApplyUnit())){
            if(CommonUtil.isMinAndMaxBetween(rule.getMinApplyAmount().intValue(), rule.getMaxApplyAmount().intValue(), totalProductAmount.intValue())) {

                PointCalculator pointCalculator = new PointCalculator(totalProductAmount,
                        rule.getAddingPointAmount(), PointFactory.getPolicy(rule.getAddingPointType()));

                point += pointCalculator.calculate().intValue();
            }
        } else if (CommonUtil.isPaymentAmountPolicyType(rule.getApplyUnit())) {
            if(CommonUtil.isMinAndMaxBetween(rule.getMinApplyAmount().intValue(), rule.getMaxApplyAmount().intValue(), paymentAmount.intValue())) {
                PointCalculator pointCalculator = new PointCalculator(paymentAmount,
                        rule.getAddingPointAmount(), PointFactory.getPolicy(rule.getAddingPointType()));

                point += pointCalculator.calculate().intValue();
            }
        }

        return point;
    }
}
