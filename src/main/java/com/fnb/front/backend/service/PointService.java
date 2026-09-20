package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.repository.MemberRepository;
import com.fnb.front.backend.repository.PointRepository;
import com.fnb.front.backend.util.CommonUtil;
import com.fnb.front.backend.util.PointType;
import com.fnb.front.backend.util.Used;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public boolean givePoint(Order order, Member member) {
        //TODO 페이에 따른 추가적립
        int point = order.getUsePoint().intValue();

        if (!member.isUsablePoint(point)) {
            return false;
        }

        int applyPoint = this.applyGradePointForOrder(member, order.getTotalAmount(),
                                BigDecimal.valueOf(order.getTotalAmount().intValue() - order.getDiscountAmount().intValue()));

        MemberPoint minusPoint = MemberPoint.builder()
                .pointType(PointType.MINUS.getValue()) // 차감
                .orderId(order.getOrderId())
                .memberId(member.getMemberId())
                .amount(point)
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
        this.memberRepository.updateMinusPoint(member.getMemberId(), point);
        this.memberRepository.updatePlusPoint(member.getMemberId(), applyPoint);
        return true;
    }

    public void returnPoint(Order order, Member member) {
        List<MemberPoint> memberPoints = this.memberRepository.findMemberPoint(order.getOrderId());

        for (MemberPoint memberPoint : memberPoints) {
            if (memberPoint.getPointType() == PointType.PLUS.getValue()) {
                this.memberRepository.updateMinusPoint(member.getMemberId(), memberPoint.getAmount());
            } else if (memberPoint.getPointType() == PointType.MINUS.getValue()) {
                this.memberRepository.updatePlusPoint(member.getMemberId(), memberPoint.getAmount());
            }
        }

        this.pointRepository.deleteMemberPoint(order.getOrderId());
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
