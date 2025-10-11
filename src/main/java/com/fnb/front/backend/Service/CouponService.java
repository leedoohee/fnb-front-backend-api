package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.response.CouponResponse;
import com.fnb.front.backend.repository.CouponRepository;
import com.fnb.front.backend.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;

    public CouponService(CouponRepository couponRepository, MemberRepository memberRepository) {
        this.couponRepository = couponRepository;
        this.memberRepository = memberRepository;
    }

    public List<CouponResponse> getCoupons() {
        List<CouponResponse> responses  = new ArrayList<>();
        List<Coupon> coupons            = this.couponRepository.findCoupons();

        for (Coupon coupon : coupons) {
            responses.add(CouponResponse.builder()
                            .couponType(coupon.getCouponType())
                            .applyStartAt(coupon.getApplyStartAt())
                            .couponName(coupon.getName())
                            .description(coupon.getDescription())
                            .couponId(coupon.getId())
                            .status(coupon.getStatus())
                            .applyEndAt(coupon.getApplyEndAt())
                            .build());
        }

        return responses;
    }

    public boolean createMemberCoupon(String memberId, int couponId) {
        Member member               = this.memberRepository.findMember(memberId);
        Coupon coupon               = this.couponRepository.findCoupon(couponId);
        MemberCoupon memberCoupon   = this.couponRepository.findMemberCoupon(member.getMemberId(), couponId);

        if(memberCoupon != null) {
            return false;
        }

        if(coupon != null && isUsableCoupon(coupon, member)) {
            return false;
        }

        MemberCoupon mCoupon = MemberCoupon.builder()
                .memberId(memberId)
                .isUsed("1")
                .createdAt(LocalDateTime.now())
                .couponId(couponId).build();

        int memberCouponId = this.couponRepository.insertMemberCoupon(mCoupon);

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

        if (coupon.isCanApplyDuring()) {
            return true;
        }

        return false;
    }
}
