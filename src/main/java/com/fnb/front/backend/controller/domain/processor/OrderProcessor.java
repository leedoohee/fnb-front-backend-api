package com.fnb.front.backend.controller.domain.processor;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.implement.DiscountPolicy;
import com.fnb.front.backend.controller.domain.implement.Calculator;
import com.fnb.front.backend.controller.dto.CreateOrderDto;
import com.fnb.front.backend.controller.dto.CreateOrderProductDto;
import com.fnb.front.backend.util.OptionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class OrderProcessor {
    private final Member member;
    private final Order order;
    private final List<Product> products;
    private final List<Coupon> coupons;

    public OrderProcessor(Member member, Order order, List<Product> products, List<Coupon> coupons) {
        this.member = member;
        this.order = order;
        this.products = products;
        this.coupons = coupons;
    }

    public CreateOrderDto buildOrder() {

        if(!this.member.isCanPurchase()) {
            return new CreateOrderDto("400", "구매 불가능한 회원입니다");
        }

        if(!this.isCanUsePoint(this.order.getUsePoint(), this.member.getPoints())) {
            return new CreateOrderDto("400", "사용 가능한 포인트를 초과하였습니다");
        }

        if(!this.isOwnedCoupons(this.member, this.coupons)) {
            return new CreateOrderDto("400", "소유하지 않은 쿠폰을 사용하였습니다");
        }

        if(!this.isCanUseCoupons(this.coupons, this.member)) {
            return new CreateOrderDto("400", "사용 불가능한 쿠폰이 포함되어 있습니다");
        }

        if(!this.isCanOrderProducts(this.products)) {
            return new CreateOrderDto("400", "구매 불가능한 상품이 포함되어 있습니다");
        }

        List<CreateOrderProductDto> createOrderProductDtos = this.buildOrderProducts(this.member, this.products, this.coupons);

        int totalCouponPrice        = this.calcTotalCouponPrice(Objects.requireNonNull(createOrderProductDtos));
        int totalMemberShipPrice    = this.calcTotalMemberShipPrice(createOrderProductDtos);
        int totalOriginPrice        = this.calcTotalOriginPrice(createOrderProductDtos);

        return CreateOrderDto.builder()
                .orderId(this.generateOrderId())
                .orderDate(LocalDateTime.now())
                .memberName(this.member.getName())
                .discountAmount(BigDecimal.valueOf(totalCouponPrice + totalMemberShipPrice).add(this.order.getUsePoint()))
                .couponAmount(totalCouponPrice)
                .orderAmount(BigDecimal.valueOf(totalOriginPrice))
                .orderProducts(createOrderProductDtos)
                .build();
    }

    private boolean isCanUsePoint(BigDecimal usedPoint, int ownedPoint) {
        return usedPoint.intValue() <= ownedPoint;
    }

    private boolean isCanUseCoupons(List<Coupon> coupons, Member member) {
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
            if(!ownedCouponIds.contains(coupon.getCouponId())) {
                return false;
            }
        }

        return true;
    }

    private boolean isCanOrderProducts(List<Product> products) {

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

    private List<CreateOrderProductDto> buildOrderProducts(Member member, List<Product> products, List<Coupon> coupons) {
        List<CreateOrderProductDto> createOrderProductDtos = new ArrayList<>();

        for (Product product : products) {
            int memberShipPrice = 0;

            if (product.inMemberShipDiscount(member)) {
                memberShipPrice = this.calcMemberShipPriceToProduct(product, member);
            }

            int couponPrice = this.calcCouponPriceToProduct(product, coupons);

            CreateOrderProductDto orderProduct = CreateOrderProductDto.builder()
                    .orderId(this.generateOrderId())
                    .orderProductId(this.generateOrderProductId())
                    .productId(product.getProductId())
                    .name(product.getName())
                    .couponId(this.applyCouponToProduct(product, coupons))
                    .quantity(product.getQuantity())
                    .originPrice(this.calcPriceWithOptions(this.calcPriceWithQuantity(product), product))
                    .couponPrice(couponPrice)
                    .discountPrice(couponPrice + memberShipPrice)
                    .build();

            createOrderProductDtos.add(orderProduct);
        }

        return createOrderProductDtos;
    }

    private int applyCouponToProduct(Product product, List<Coupon> coupons) {
        return coupons.stream()
                .filter(coupon -> Objects.equals(coupon.getApplyProductId(), product.getProductId()))
                .map(Coupon::getCouponId)
                .mapToInt(Integer::intValue).findFirst().orElse(0);
    }

    private int calcCouponPriceToProduct(Product product, List<Coupon> coupons) {
        return coupons.stream()
            .filter(coupon -> Objects.equals(coupon.getApplyProductId(), product.getProductId() ))
            .map(coupon -> {

                DiscountPolicy couponPolicy = DiscountFactory.getPolicy(coupon.getDiscountType());
                CouponCalculator couponPriceCalculator = null;

                if (couponPolicy != null) {
                    couponPriceCalculator = new CouponCalculator(coupon, this.calcPriceWithQuantity(product), couponPolicy);
                }

                return Objects.requireNonNull(couponPriceCalculator).calculate();
            }).mapToInt(BigDecimal::intValue).findFirst().orElse(0);
    }

    private int calcMemberShipPriceToProduct(Product product, Member member) {
        Calculator memberShipCalculator = null;

        DiscountPolicy memberShipPolicy = DiscountFactory.getPolicy(product.getApplyMemberGradeDisType());

        if (memberShipPolicy != null) {
            memberShipCalculator = new MemberShipCalculator(member,
                                        this.calcPriceWithQuantity(product),
                                        product.getApplyMemberGradeDisAmt().intValue(), memberShipPolicy);
        }

        return Objects.requireNonNull(memberShipCalculator).calculate().intValue();
    }

    private int calcTotalCouponPrice(List<CreateOrderProductDto> createOrderProductDtos) {
        return createOrderProductDtos.stream()
                .map(CreateOrderProductDto::getCouponPrice)
                .mapToInt(Integer::intValue).sum();
    }

    private int calcTotalMemberShipPrice(List<CreateOrderProductDto> createOrderProductDtos) {
        return createOrderProductDtos.stream()
                .map(CreateOrderProductDto::getMemberShipPrice)
                .mapToInt(Integer::intValue).sum();
    }

    private int calcTotalOriginPrice(List<CreateOrderProductDto> createOrderProductDtos) {
        return createOrderProductDtos.stream()
                .map(CreateOrderProductDto::getOriginPrice)
                .mapToInt(Integer::intValue).sum();
    }

    private int calcPriceWithQuantity(Product product) {
        List<ProductOption> productOptions  = product.getProductOption();
        ProductOption singleOption          = productOptions.stream()
                                                .filter(productOption -> productOption.getOptionType().equals(OptionType.BASIC.getValue()))
                                                .findFirst().orElse(null);

        return (product.getPrice().intValue() + Objects.requireNonNull(singleOption).getPrice()) * product.getQuantity();
    }

    private int calcPriceWithOptions(int optionPrice, Product product) {
        int sumPrice = product.getProductOption().stream()
                            .filter(productOption -> productOption.getOptionType().equals(OptionType.ADDITIONAL.getValue()))
                            .map(ProductOption::getPrice)
                            .mapToInt(Integer::intValue).sum();

        return optionPrice + sumPrice;
    }

    public String generateOrderId() {
        return "ORDER_" + new Date().getTime();
    }

    public String generateOrderProductId() {
        return "ORDER_PRODUCT" + new Date().getTime();
    }
}
