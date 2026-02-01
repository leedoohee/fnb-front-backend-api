package com.fnb.front.backend.controller.domain.validator;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.request.OrderProductRequest;

import java.math.BigDecimal;
import java.util.HashSet;
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
        List<Integer> ownedCouponIds    = ownedCoupons.stream().map(MemberCoupon::getCouponId).toList();

        for (Coupon coupon : coupons) {
            if(!ownedCouponIds.contains(coupon.getCouponId())) {
                return false;
            }
        }

        return true;
    }

    public boolean isCanOrderProducts(List<Product> products, List<ProductOption> options) {

        List<Integer> aliveProductIds     = options.stream().map(ProductOption::getProductId).distinct().toList();
        List<Integer> aliveOptionIds      = options.stream().map(ProductOption::getProductOptionId).distinct().toList();
        List<Integer> orderProductIds     = products.stream().map(Product::getProductId).distinct().toList();
        List<Integer> orderOptionIds      = products.stream().flatMap(product -> product.getProductOption()
                                                    .stream().map(ProductOption::getProductOptionId)).toList();
        HashSet<Integer> aliveSet         = new HashSet<>(aliveProductIds);
        HashSet<Integer> orderSet         = new HashSet<>(orderProductIds);

        if (!aliveSet.containsAll(orderSet)) {
            return false;
        }

        if(orderOptionIds.size() > aliveOptionIds.size()) {
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
