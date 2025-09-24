package com.fnb.backend.controller.domain.processor;

import com.fnb.backend.controller.domain.*;
import com.fnb.backend.controller.domain.implement.DiscountPolicy;
import com.fnb.backend.controller.domain.implement.Calculator;
import com.fnb.backend.controller.dto.CreateOrderDto;
import com.fnb.backend.controller.dto.CreateOrderProductDto;

import java.math.BigDecimal;
import java.util.*;

public class OrderProcessor {
    private final Member member;
    private final Merchant merchant;
    private final Order order;
    private final List<Product> products;
    private final List<Coupon> coupons;

    public OrderProcessor(Merchant merchant, Member member, Order order, List<Product> products, List<Coupon> coupons) {
        this.merchant = merchant;
        this.member = member;
        this.order = order;
        this.products = products;
        this.coupons = coupons;
    }

    public CreateOrderDto buildOrder() throws Exception {

        if(!this.merchant.isLive()) {
            return null;
        }

        if(!this.member.isCanPurchase()) {
            return null;
        }

        if(!this.validatePoint(this.order.getUsePoint(), this.member.getOwnedPointAmount())) {
            return null;
        }

        if(!this.isOwnedCoupons(this.member, this.coupons)){
            return null;
        }

        if(!this.validateCoupons(this.coupons, this.member)){
            return null;
        }

        if(!this.validateOrderProduct(this.products)) {
            return null;
        }

        List<CreateOrderProductDto> createOrderProductDtos = this.buildOrderProducts(this.member, this.products, this.coupons);

        int totalCouponPrice        = this.calculateTotalCouponPrice(createOrderProductDtos);
        int totalMemberShipPrice    = this.calculateTotalMemberShipPrice(createOrderProductDtos);
        int totalOriginPrice        = this.calculateTotalOriginPrice(createOrderProductDtos);

        return CreateOrderDto.builder()
                .orderId(this.generateOrderId(this.order.getMerchantId()))
                .orderDate(this.order.getOrderDate())
                .merchantId(this.order.getMerchantId())
                .discountAmount(BigDecimal.valueOf(totalCouponPrice + totalMemberShipPrice).add(this.order.getUsePoint()))
                .couponAmount(BigDecimal.valueOf(totalCouponPrice))
                .orderAmount(BigDecimal.valueOf(totalOriginPrice))
                .orderProducts(createOrderProductDtos)
                .build();
    }

    private boolean validatePoint(BigDecimal point, int ownedPoint) {
        return point.intValue() - ownedPoint < 0;
    }

    private boolean validateCoupons(List<Coupon> coupons, Member member) {
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

    private boolean isOwnedCoupons(Member member, List<Coupon> coupons) {
        List<MemberCoupon> ownedCoupons = member.getOwnedCoupon();
        List<Integer> ownedCouponIds = ownedCoupons.stream().map(MemberCoupon::getCouponId).toList();

        for (Coupon coupon : coupons) {
            if(!ownedCouponIds.contains(coupon.getId())) {
                return false;
            }
        }

        return true;
    }

    private boolean validateOrderProduct(List<Product> products) {

        if(products.isEmpty()) {
            return false;
        }

        for (Product product : products) {
            if(!product.isAvailablePurchase()) {
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

    private List<CreateOrderProductDto> buildOrderProducts(Member member, List<Product> products, List<Coupon> coupons) {
        List<CreateOrderProductDto> createOrderProductDtos = new ArrayList<>();

        for (Product product : products) {
            int memberShipPrice = 0;

            if(!product.isAvailableUseCoupon()) {
                return null;
            }

            if (product.inMemberShipDiscount(member)) {
                memberShipPrice = this.calculateMemberShipToProduct(product, member);
            }

            int couponPrice = this.calculateCouponToProduct(product, coupons);

            CreateOrderProductDto orderProduct = CreateOrderProductDto.builder()
                    .orderId(this.generateOrderId(product.getMerchantId()))
                    .orderProductId(this.generateOrderProductId(product.getMerchantId(), String.valueOf(product.getId())))
                    .productId(product.getId())
                    .name(product.getName())
                    .couponId(this.applyCouponToProduct(product, coupons))
                    .quantity(product.getPurchaseQuantity())
                    .originPrice(this.calcPriceWithAdditionalOptions(this.calculatePriceWithQuantity(product), product.getAdditionalOptions()))
                    .couponPrice(couponPrice)
                    .discountPrice(couponPrice + memberShipPrice)
                    .build();

            createOrderProductDtos.add(orderProduct);
        }

        return createOrderProductDtos;
    }

    private int applyCouponToProduct(Product product, List<Coupon> coupons) {
        return coupons.stream()
                .filter(coupon -> Objects.equals(coupon.getApplyProductId(), product.getId()))
                .map(Coupon::getId)
                .mapToInt(Integer::intValue).findFirst().orElse(0);
    }

    private int calculateCouponToProduct(Product product, List<Coupon> coupons) {
        return coupons.stream()
            .filter(coupon -> Objects.equals(coupon.getApplyProductId(), product.getId() ))
            .map(coupon -> {

                DiscountPolicy couponPolicy = DiscountFactory.getPolicy(coupon.getDiscountType());
                CouponCalculator couponPriceCalculator = null;

                if (couponPolicy != null) {
                    couponPriceCalculator = new CouponCalculator(coupon, this.calculatePriceWithQuantity(product), couponPolicy);
                }

                return Objects.requireNonNull(couponPriceCalculator).calculate();
            }).mapToInt(BigDecimal::intValue).findFirst().orElse(0);
    }

    private int calculateMemberShipToProduct(Product product, Member member) {
        Calculator memberShipCalculator = null;

        DiscountPolicy memberShipPolicy = DiscountFactory.getPolicy(product.getApplyMemberGradeDisType());

        if (memberShipPolicy != null) {
            memberShipCalculator = new MemberShipCalculator(member,
                                        this.calculatePriceWithQuantity(product), product.getApplyMemberGradeDisAmt().intValue(), memberShipPolicy);
        }

        return Objects.requireNonNull(memberShipCalculator).calculate().intValue();
    }

    private int calculateTotalCouponPrice(List<CreateOrderProductDto> createOrderProductDtos) {
        return createOrderProductDtos.stream()
                .map(CreateOrderProductDto::getCouponPrice)
                .mapToInt(Integer::intValue).sum();
    }

    private int calculateTotalMemberShipPrice(List<CreateOrderProductDto> createOrderProductDtos) {
        return createOrderProductDtos.stream()
                .map(CreateOrderProductDto::getMemberShipPrice)
                .mapToInt(Integer::intValue).sum();
    }

    private int calculateTotalOriginPrice(List<CreateOrderProductDto> createOrderProductDtos) {
        return createOrderProductDtos.stream()
                .map(CreateOrderProductDto::getOriginPrice)
                .mapToInt(Integer::intValue).sum();
    }

    private int calculatePriceWithQuantity(Product product) {
        // 주문생성시 , 1대1 구조라 무조건 하나일 수 밖에 없다.
        ProductOption productOptions = product.getProductOption();
        BigDecimal optionPrice = productOptions.getOptionPrice();

        return (product.getPrice() + optionPrice.intValue()) * product.getPurchaseQuantity();
    }

    private int calcPriceWithAdditionalOptions(int optionPrice, List<AdditionalOption> additionalOptions) {
        int sumPrice = additionalOptions.stream().map(AdditionalOption::getPrice).mapToInt(BigDecimal::intValue).sum();
        return optionPrice + sumPrice;
    }

    private String generateOrderProductId(String merchantId, String productId) {
        return "ORDER_" + merchantId + "_" + productId + "_"  + new Date().getTime();
    }

    public String generateOrderId(String merchantId) {
        return "ORDER_" + merchantId + "_" + new Date().getTime();
    }
}
