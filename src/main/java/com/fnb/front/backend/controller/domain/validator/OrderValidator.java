package com.fnb.front.backend.controller.domain.validator;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.request.OrderCouponRequest;
import com.fnb.front.backend.controller.domain.request.OrderProductRequest;
import com.fnb.front.backend.controller.domain.request.OrderRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component
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

    public boolean isCanPurchaseProduct(List<Product> products, List<ProductOption> options) {
        List<Integer> aliveProductIds     = options.stream().map(ProductOption::getProductId).distinct().toList();
        List<Integer> orderProductIds     = products.stream().map(Product::getProductId).distinct().toList();

        HashSet<Integer> aliveProductSet  = new HashSet<>(aliveProductIds);
        HashSet<Integer> orderProductSet  = new HashSet<>(orderProductIds);

        return aliveProductSet.containsAll(orderProductSet);
    }

    public boolean isCanPurchaseOption(List<Product> products, List<ProductOption> options) {
        List<Integer> aliveOptionIds      = options.stream().map(ProductOption::getProductOptionId).distinct().toList();
        List<Integer> orderOptionIds      = products.stream().flatMap(product -> product.getProductOption()
                .stream().map(ProductOption::getProductOptionId)).distinct().toList();

        return orderOptionIds.size() == aliveOptionIds.size();
    }

    public boolean isCanOrderProducts(List<Product> products, List<ProductOption> options,
                                      OrderRequest orderRequest, List<Coupon> coupons) {
        if (!isCanPurchaseProduct(products, options)) {
            return false;
        }

        if(!isCanPurchaseOption(products, options)) {
            return false;
        }

        for (Product product : products) {
            if (product.isInfiniteQuantity()) {
                continue;
            }

            if (!product.isAvailablePurchase()) {
                return false;
            }

            if(!coupons.isEmpty()) {
                if (!this.isCanApplyCouponToProduct(product, coupons, orderRequest.getOrderCouponRequests())
                    && product.isAvailableUseCoupon()) {
                    return false;
                }
            }

            if (product.isLessMinPurchaseQuantity(this.getOrderProductQuantity(product, orderRequest.getOrderProductRequests()))) {
                return false;
            }

            if (product.isOverMaxPurchaseQuantity(this.getOrderProductQuantity(product, orderRequest.getOrderProductRequests()))) {
                return false;
            }

            if (!product.isOrderableQuantity(this.getOrderProductQuantity(product, orderRequest.getOrderProductRequests()))) {
                return false;
            }
        }

        return true;
    }

    public Integer getOrderProductQuantity(Product product, List<OrderProductRequest> orderProductRequests) {
        OrderProductRequest request = orderProductRequests.stream()
                .filter(orderProductRequest -> product.getProductId() == orderProductRequest.getProductId())
                .findFirst().orElse(null);

        if (request == null) {
            return 0;
        } else {
            return request.getQuantity();
        }
    }

    public boolean isCanApplyCouponToProduct(Product product, List<Coupon> coupons, List<OrderCouponRequest> orderCouponRequests) {
        OrderCouponRequest couponRequest = orderCouponRequests.stream()
                .filter(orderCouponRequest -> product.getProductId() == orderCouponRequest.getProductId())
                .findFirst().orElse(null);

        Coupon coupon = coupons.stream()
                .filter(c -> c.getCouponId() == Objects.requireNonNull(couponRequest).getCouponId())
                .findFirst().orElse(null);

        if (couponRequest == null) {
            return false;
        }

        if (coupon == null) {
            return false;
        }

        List<CouponProduct> couponProducts = coupon.getCouponProducts();

        for (CouponProduct couponProduct : couponProducts) {
            if (couponProduct.getProductId() == product.getProductId()) {
                return true;
            }
        }

        return false;
    }
}
