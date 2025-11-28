package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.response.CouponResponse;
import com.fnb.front.backend.repository.CouponRepository;
import com.fnb.front.backend.repository.MemberRepository;
import com.fnb.front.backend.repository.OrderRepository;
import com.fnb.front.backend.util.CouponStatus;
import com.fnb.front.backend.util.Used;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    private final MemberRepository memberRepository;

    private final OrderRepository orderRepository;

    public List<Coupon> findCoupons(List<Integer> couponIds) {
        return this.couponRepository.findCoupons(couponIds);
    }

    public List<CouponResponse> getCoupons() {
        return this.couponRepository.findCoupons(CouponStatus.AVAILABLE.getValue())
            .stream()
            .map(coupon -> CouponResponse.builder()
                    .couponType(coupon.getCouponType())
                    .applyStartAt(coupon.getApplyStartAt())
                    .couponName(coupon.getName())
                    .description(coupon.getDescription())
                    .couponId(coupon.getCouponId())
                    .status(coupon.getStatus())
                    .applyEndAt(coupon.getApplyEndAt())
                    .build()
            )
            .collect(Collectors.toList());
    }

    public boolean createMemberCoupon(String memberId, int couponId) {
        Member member               = this.memberRepository.findMember(memberId);
        Coupon coupon               = this.couponRepository.findCoupon(couponId);
        MemberCoupon memberCoupon   = this.couponRepository.findMemberCoupon(member.getMemberId(), couponId);

        assert coupon != null : "존재하지 않는 쿠폰입니다.";

        boolean couponResult = isUsableCoupon(coupon, member);

        assert couponResult : "소유할 수 없는 상태의 쿠폰입니다.";

        if (memberCoupon != null) {
            throw new IllegalStateException("이미 소유하고 있거나 사용한 쿠폰입니다.");
        }

        MemberCoupon newCoupon = MemberCoupon.builder()
                .memberId(memberId)
                .isUsed(Used.NOTUSED.getValue())
                .createdAt(LocalDateTime.now())
                .couponId(couponId).build();

        this.couponRepository.insertMemberCoupon(newCoupon);

        return true;
    }

    public boolean applyCouponToProduct(String memberId, int couponId, int productId) {
        MemberCoupon memberCoupon = this.couponRepository.findMemberCoupon(memberId, couponId, Used.NOTUSED.getValue());

        assert memberCoupon != null : "소유하지 않았거나 사용한 쿠폰입니다.";

        CouponProduct couponProduct = memberCoupon.getCoupon().getCouponProducts().stream()
                .filter(element -> element.getProductId() == productId).findFirst().orElse(null);

        if (couponProduct == null) {
            throw new IllegalStateException("쿠폰적용이 불가능한 상품입니다.");
        }

        return isUsableCoupon(memberCoupon.getCoupon(), memberCoupon.getMember());
    }

    public boolean subtractCoupon(Order order, Member member) {
        List<OrderProduct> orderProducts = order.getOrderProducts();
        List<Integer> couponIdList       = orderProducts.stream().map(OrderProduct::getCouponId).toList();
        List<MemberCoupon> memberCoupons = this.memberRepository.findMemberCoupons(member.getMemberId(), couponIdList);

        for (OrderProduct orderProduct : orderProducts) {
            int couponId = orderProduct.getCouponId();

            MemberCoupon memberCoupon = memberCoupons.stream()
                    .filter(coupon -> coupon.getCouponId() == couponId).findFirst().orElse(null);

            if (memberCoupon != null && !memberCoupon.getIsUsed().equals(Used.NOTUSED.getValue())) {
                return false;
            }

            this.couponRepository.updateUsedMemberCoupon(member.getMemberId(), couponId, Used.NOTUSED.getValue());
        }

        return true;
    }

    public void returnCoupon(Order order, Member member) {
        List<OrderProduct> orderProducts = this.orderRepository.findOrderProducts(order.getOrderId());

        for (OrderProduct orderProduct : orderProducts) {

            if (orderProduct.getCoupon() == null) {
                continue;
            }

            this.couponRepository.updateUsedMemberCoupon(member.getMemberId(),
                    orderProduct.getCoupon().getCouponId(), Used.NOTUSED.getValue());
        }
    }

    private boolean isUsableCoupon(Coupon coupon, Member member) {
        return coupon.isAvailableStatus() && coupon.isBelongToAvailableGrade(member) && coupon.isCanApplyDuring();
    }
}
