package com.fnb.front.backend.controller.domain.validator;

import com.fnb.front.backend.controller.domain.Coupon;
import com.fnb.front.backend.controller.domain.Member;
import com.fnb.front.backend.controller.domain.MemberCoupon;
import com.fnb.front.backend.controller.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public class OrderValidator {

    public boolean isCanPurchaseMember(Member member) {
        return member.isCanPurchase();
    }

    public boolean isCanUsePoint(BigDecimal usedPoint, int ownedPoint) {
        return usedPoint.intValue() <= ownedPoint;
    }

    public boolean isCanUseCoupons(List<Coupon> coupons, Member member) {
        for(Coupon coupon : coupons) {
            if(!coupon.isAvailableStatus()) {
                return false;
            }

            if(!coupon.isCanApplyDuring()) {
                return false;
            }

            if(!coupon.isBelongToAvailableGrade(member)) {
                return false;
            }
        }

        return true;
    }

    public boolean isOwnedCoupons(Member member, List<Coupon> coupons) {
        List<MemberCoupon> ownedCoupons = member.getOwnedCoupon();
        List<Integer> ownedCouponIds = ownedCoupons.stream().map(MemberCoupon::getCouponId).toList();

        for (Coupon coupon : coupons) {
            if(!ownedCouponIds.contains(coupon.getCouponId())) {
                return false;
            }
        }

        return true;
    }

    public boolean isCanOrderProducts(List<Product> products) {

        if(products.isEmpty()) {
            return false;
        }

        for (Product product : products) {
            if (product.isInfiniteQty()) {
                continue;
            }

            if (!product.isAvailablePurchase()) {
                return false;
            }

            if (!product.isAvailableUseCoupon()) {
                return false;
            }

            if(product.isLessMinPurchaseQuantity()) {
                return false;
            }

            if(product.isOverMaxPurchaseQuantity()) {
                return false;
            }
        }

        return true;
    }
}
