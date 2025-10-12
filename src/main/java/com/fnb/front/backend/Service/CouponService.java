package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.response.CouponResponse;
import com.fnb.front.backend.repository.CouponRepository;
import com.fnb.front.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    private final MemberRepository memberRepository;

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

        assert memberCoupon == null : "이미 소유한 쿠폰입니다";

        assert (coupon != null && isUsableCoupon(coupon, member)): "소유할 수 없는 쿠폰입니다.";

        MemberCoupon mCoupon = MemberCoupon.builder()
                .memberId(memberId)
                .isUsed("1")
                .createdAt(LocalDateTime.now())
                .couponId(couponId).build();

        this.couponRepository.insertMemberCoupon(mCoupon);

        return true;
    }

    public boolean applyCouponToProduct(String memberId, int couponId, int productId) {
        MemberCoupon memberCoupon = this.couponRepository.findMemberCoupon(memberId, couponId);

        assert memberCoupon != null : "보유하지 않은 쿠폰입니다.";

        if(memberCoupon.getCoupon().isApplyToEntireProduct()) {
            return true;
        }

        if (!memberCoupon.getCoupon().isApplyToEntireProduct()) {
            CouponProduct couponProduct = memberCoupon.getCoupon().getCouponProducts().stream()
                    .filter(element -> element.getProductId() == productId).findFirst().orElse(null);

            assert couponProduct != null : "쿠폰적용이 불가능한 상품입니다.";
        }

        assert isUsableCoupon(memberCoupon.getCoupon(), memberCoupon.getMember()) : "적용불가능한 쿠폰입니다.";

        return true;
    }

    private boolean isUsableCoupon(Coupon coupon, Member member) {
        if (!coupon.isAvailableStatus()) {
            return false;
        }

        if (!coupon.isBelongToAvailableGrade(member)) {
            return false;
        }

        if (!coupon.isCanApplyDuring()) {
            return false;
        }

        return true;
    }
}
