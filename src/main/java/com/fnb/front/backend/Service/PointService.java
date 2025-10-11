package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.event.OrderResultEvent;
import com.fnb.front.backend.repository.PointRepository;
import com.fnb.front.backend.util.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

@Service
public class PointService {

    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handlePointToOrder(OrderResultEvent event) {
        Member member = event.getOrder().getMember();
        //TODO 페이에 따른 추가적립
        int applyPoint = this.applyPointForOrder(member, event.getTotalProductAmount(), event.getPaymentAmount());

        if(!member.isUsablePoint(member.getPoints())) {
            throw new RuntimeException("포인트 부족");
        }

        BigDecimal usePoint = event.getOrder().getUsePoint();

        MemberPoint minusPoint = MemberPoint.builder()
                .pointType(0) // 차감
                .orderId(event.getOrder().getOrderId())
                .memberId(member.getId())
                .amount(usePoint.intValue())
                .isUsed("1")
                .build();

        this.pointRepository.insertMemberPoint(minusPoint);

        MemberPoint plusPoint = MemberPoint.builder()
                                .pointType(1) // 적립
                                .orderId(event.getOrder().getOrderId())
                                .memberId(member.getId())
                                .amount(applyPoint)
                                .isUsed("1")
                                .build();

        this.pointRepository.insertMemberPoint(plusPoint);
    }

    private int applyPointForOrder(Member member, BigDecimal totalProductAmount, BigDecimal paymentAmount) {
        MemberPointRule rule = member.getMemberGrade().getMemberPointRule();
        int point = 0;

        if(CommonUtil.isProductAmountPolicyType(rule.getApplyUnit())){
            if(CommonUtil.isMinAndMaxBetween(rule.getMinApplyAmount().intValue(), rule.getMaxApplyAmount().intValue(), totalProductAmount.intValue())) {
                PointCalculator pointCalculator = new PointCalculator(totalProductAmount,
                        rule.getAddingPointAmount(), PointFactory.getPolicy(rule.getAddingPointType()));

                point += pointCalculator.calculate().intValue();
            }
        }

        if(CommonUtil.isPaymentAmountPolicyType(rule.getApplyUnit())) {
            if(CommonUtil.isMinAndMaxBetween(rule.getMinApplyAmount().intValue(), rule.getMaxApplyAmount().intValue(), paymentAmount.intValue())) {
                PointCalculator pointCalculator = new PointCalculator(paymentAmount,
                        rule.getAddingPointAmount(), PointFactory.getPolicy(rule.getAddingPointType()));

                point += pointCalculator.calculate().intValue();
            }
        }

        return point;
    }
}
