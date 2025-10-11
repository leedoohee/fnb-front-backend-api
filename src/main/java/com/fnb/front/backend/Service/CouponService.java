package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.event.OrderResultEvent;
import com.fnb.front.backend.repository.CouponRepository;
import com.fnb.front.backend.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
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
    public void handleCouponToOrder(OrderResultEvent event) {
        Member member                    = event.getOrder().getMember();
        List<OrderProduct> orderProducts = event.getOrder().getOrderProducts();
        List<Integer> couponIdList       = orderProducts.stream().map(OrderProduct::getCouponId).toList();
        List<MemberCoupon> memberCoupons = this.memberRepository.findMemberCoupons(member.getMemberId(), couponIdList);

        for (OrderProduct orderProduct : orderProducts) {
            int couponId = orderProduct.getCouponId();

            MemberCoupon memberCoupon = memberCoupons.stream()
                    .filter(coupon -> coupon.getCouponId() == couponId).findFirst().orElse(null);

            if (memberCoupon != null && !memberCoupon.getIsUsed().equals("1")) {
                throw new RuntimeException("이미 사용된 쿠폰입니다.");
            }

            this.couponRepository.updateUsedMemberCoupon(member.getMemberId(), couponId);
        }
    }

    public boolean createMemberCoupon(String memberId, int couponId) {
        Member member = this.memberRepository.findMember(memberId);
        Coupon coupon = this.couponRepository.findCoupon(couponId);

        if(coupon != null && isUsableCoupon(coupon, member)) {
            return false;
        }

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .memberId(memberId)
                .isUsed("1")
                .createdAt(LocalDateTime.now())
                .couponId(couponId).build();

        int memberCouponId = this.couponRepository.insertMemberCoupon(memberCoupon);

        if (memberCouponId <= 0) {
            return false;
        }

        return true;
    }

    public boolean applyCouponToProduct(String memberId, int couponId, int productId) {
        MemberCoupon memberCoupon = this.couponRepository.findMemberCoupon(memberId, couponId);

        if(memberCoupon == null) {
            return false;
        }

        if(!memberCoupon.getCoupon().isApplyToEntireProduct() && memberCoupon.getCoupon().getCouponProducts().isEmpty()) {
            return false;
        }

        if(!memberCoupon.getCoupon().isApplyToEntireProduct()) {
            CouponProduct couponProduct = memberCoupon.getCoupon().getCouponProducts().stream()
                    .filter(coupon -> coupon.getProductId() == productId).findFirst().orElse(null);

            if (couponProduct == null) {
                return false;
            }
        }

        if (memberCoupon.getCoupon() != null && isUsableCoupon(memberCoupon.getCoupon(), memberCoupon.getMember())) {
            return false;
        }

        return true;
    }

    private boolean isUsableCoupon(Coupon coupon, Member member) {
        if (coupon.isAvailableStatus()) {
            return true;
        }

        if (coupon.isBelongToAvailableGrade(member)) {
            return true;
        }

        return false;
    }
}
