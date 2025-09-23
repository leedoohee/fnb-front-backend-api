package com.fnb.backend.Service;

import com.fnb.backend.controller.domain.*;
import com.fnb.backend.controller.domain.event.OrderResultEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    @EventListener
    public void handlePointToOrderMember(OrderResultEvent event) {
        Member member = event.getMember();
        MemberPointRule rule = member.getMemberGrade().getMemberPointRule();

        int point = 0;

        if(rule.getApplyUnit().equals("PRODUCT")){
            if(rule.getMinApplyAmount().intValue() <= event.getTotalProductAmount().intValue() && event.getTotalProductAmount().intValue() <= rule.getMaxApplyAmount().intValue()) {
                PointCalculator pointCalculator = new PointCalculator(event.getTotalProductAmount(),
                                                        rule.getAddingPointAmount(), PointFactory.getPolicy(rule.getAddingPointType()));

                point += pointCalculator.calculate().intValue();
            }
        } else {
            if(rule.getMinApplyAmount().intValue() <= event.getPaymentAmount().intValue() && event.getPaymentAmount().intValue() <= rule.getMaxApplyAmount().intValue()) {
                PointCalculator pointCalculator = new PointCalculator(event.getPaymentAmount(),
                        rule.getAddingPointAmount(), PointFactory.getPolicy(rule.getAddingPointType()));

                point += pointCalculator.calculate().intValue();
            }
        }
    }
}
