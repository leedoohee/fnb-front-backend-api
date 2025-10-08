package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.Member;
import com.fnb.front.backend.controller.domain.MemberCoupon;
import com.fnb.front.backend.controller.domain.OrderProduct;
import com.fnb.front.backend.controller.domain.event.OrderResultEvent;
import com.fnb.front.backend.repository.CouponRepository;
import com.fnb.front.backend.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import java.util.List;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;

    public CouponService(CouponRepository couponRepository, MemberRepository memberRepository) {
        this.couponRepository = couponRepository;
        this.memberRepository = memberRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handlePointToOrder(OrderResultEvent event) {
        Member member                    = event.getMember();
        List<OrderProduct> orderProducts = event.getOrderProducts();
        List<Integer> couponIdList       = orderProducts.stream().map(OrderProduct::getCouponId).toList();
        List<MemberCoupon> memberCoupons = this.memberRepository.findMemberCoupons(member.getMemberId(), couponIdList);

        for (OrderProduct orderProduct : orderProducts) {
            int couponId = orderProduct.getCouponId();

            MemberCoupon memberCoupon = memberCoupons.stream().filter(coupon -> coupon.getCouponId() == couponId).findFirst().orElse(null);
            if (memberCoupon != null && memberCoupon.getIsUsed().equals("1")) {
                this.couponRepository.updateUsedMemberCoupon(member.getMemberId(), couponId);
            } else {
                throw new RuntimeException("이미 사용된 쿠폰입니다.");
            }
        }
    }
}
