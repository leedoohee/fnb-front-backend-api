package com.fnb.front.backend.controller.domain.processor;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.implement.DiscountPolicy;
import com.fnb.front.backend.controller.domain.implement.Calculator;
import com.fnb.front.backend.controller.domain.validator.OrderValidator;
import com.fnb.front.backend.controller.dto.CreateOrderDto;
import com.fnb.front.backend.controller.dto.CreateOrderProductDto;
import com.fnb.front.backend.util.CommonUtil;
import com.fnb.front.backend.util.OptionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class OrderProcessor {
    private final Member member;
    private final Order order;
    private final List<Product> products;
    private final List<Coupon> coupons;
    private final OrderValidator orderValidator;

    public OrderProcessor(Member member, Order order, List<Product> products, List<Coupon> coupons, OrderValidator orderValidator) {
        this.member = member;
        this.order = order;
        this.products = products;
        this.coupons = coupons;
        this.orderValidator = orderValidator;
    }

    public CreateOrderDto buildOrder() {

        if (!this.orderValidator.isCanPurchaseMember(this.member)) {
            throw new IllegalStateException("구매 불가능한 회원입니다.");
        }

        if (!this.orderValidator.isCanUsePoint(this.order.getUsePoint(), this.member.getPoints())) {
            throw new IllegalStateException("사용 가능한 포인트를 초과하였습니다.");
        }

        if (!this.orderValidator.isOwnedCoupons(this.member, this.coupons)) {
            throw new IllegalStateException("소유하지 않은 쿠폰을 사용하였습니다.");
        }

        if (!this.orderValidator.isCanUseCoupons(this.coupons, this.member)) {
            throw new IllegalStateException("사용 불가능한 쿠폰이 포함되어 있습니다.");
        }

        if (!this.orderValidator.isCanOrderProducts(this.products)) {
            throw new IllegalStateException("구매 불가능한 상품이 포함되어 있습니다.");
        }

        String orderId              = CommonUtil.generateOrderId();
        int totalCouponPrice        = this.calcTotalCouponPrice(this.products, this.coupons);
        int totalMemberShipPrice    = this.calcTotalMemberShipPrice(this.member, this.products);
        int totalOriginPrice        = this.calcTotalOriginPrice(this.products);

        return CreateOrderDto.builder()
                .orderId(orderId)
                .orderDate(LocalDateTime.now())
                .memberName(this.member.getName())
                .discountAmount(totalCouponPrice + totalMemberShipPrice + this.order.getUsePoint().intValue())
                .couponAmount(totalCouponPrice)
                .orderAmount(totalOriginPrice)
                .orderProducts(this.buildOrderProducts(orderId, this.member, this.products, this.coupons))
                .build();
    }

    private List<CreateOrderProductDto> buildOrderProducts(String orderId, Member member, List<Product> products, List<Coupon> coupons) {
        List<CreateOrderProductDto> createOrderProductDtos = new ArrayList<>();

        for (Product product : products) {
            List<ProductOption> productOptions = product.getProductOption();

            int memberShipPrice = 0;
            int basicOptionWithPrice = product.getPrice().intValue() + this.getBasicOptionPrice(productOptions);

            if (product.inMemberShipDiscount(member)) {
                memberShipPrice = this.calcMemberShipPriceToProduct(product, member);
            }

            int couponPrice = this.calcCouponPriceToProduct(product, coupons);

            CreateOrderProductDto orderProduct = CreateOrderProductDto.builder()
                    .orderId(orderId)
                    .productId(product.getProductId())
                    .name(product.getName())
                    .couponId(this.applyCouponToProduct(product, coupons))
                    .quantity(product.getQuantity())
                    .originPrice(this.calcPriceWithQuantity(basicOptionWithPrice, product.getQuantity())
                                        + this.getTotalAddOptionPrice(productOptions))
                    .couponPrice(couponPrice)
                    .discountPrice(couponPrice + memberShipPrice)
                    .build();

            createOrderProductDtos.add(orderProduct);
        }

        return createOrderProductDtos;
    }

    private int applyCouponToProduct(Product product, List<Coupon> coupons) {
        return coupons.stream()
                .filter(coupon -> coupon.getApplyProductId() == product.getProductId())
                .map(Coupon::getCouponId)
                .mapToInt(Integer::intValue).findFirst().orElse(0);
    }

    private int calcCouponPriceToProduct(Product product, List<Coupon> coupons) {
        return coupons.stream()
            .filter(coupon -> coupon.getApplyProductId() == product.getProductId())
            .map(coupon -> {
                BigDecimal price = BigDecimal.valueOf(0);

                DiscountPolicy couponPolicy = DiscountFactory.getPolicy(coupon.getDiscountType());

                if (couponPolicy != null) {
                    int priceWithQuantity = this.calcPriceWithQuantity(product.getPrice().intValue(), product.getQuantity());
                    CouponCalculator couponPriceCalculator = new CouponCalculator(coupon, priceWithQuantity, couponPolicy);
                    price = couponPriceCalculator.calculate();
                }

                return price;

            }).mapToInt(BigDecimal::intValue).findFirst().orElse(0);
    }

    private int calcMemberShipPriceToProduct(Product product, Member member) {
        int price = 0;

        DiscountPolicy memberShipPolicy = DiscountFactory.getPolicy(product.getApplyMemberGradeDisType());

        if (memberShipPolicy != null) {
            Calculator memberShipCalculator = new MemberShipCalculator(member,
                                this.calcPriceWithQuantity(product.getPrice().intValue(), product.getQuantity()),
                                product.getApplyMemberGradeDisAmt().intValue(), memberShipPolicy);

            price = memberShipCalculator.calculate().intValue();
        }

        return price;
    }

    private int calcTotalCouponPrice(List<Product> products, List<Coupon> coupons) {
        int price = 0;

        for (Product product : products) {
            price += this.calcCouponPriceToProduct(product, coupons);
        }

        return price;
    }

    private int calcTotalMemberShipPrice(Member member, List<Product> products) {
        int price = 0;

        for (Product product : products) {
            if (product.inMemberShipDiscount(member)) {
                price += this.calcMemberShipPriceToProduct(product, member);
            }
        }

        return price;
    }

    private int calcTotalOriginPrice(List<Product> products) {
        int price = 0;

        for (Product product : products) {
            List<ProductOption> productOptions = product.getProductOption();
            int basicOptionWithPrice = product.getPrice().intValue() + this.getBasicOptionPrice(productOptions);
            price += (this.calcPriceWithQuantity(basicOptionWithPrice, product.getQuantity())
                                        + this.getTotalAddOptionPrice(productOptions));
        }

        return price;
    }

    private int calcPriceWithQuantity(int price, int quantity) {
        return price * quantity;
    }

    private int getBasicOptionPrice(List<ProductOption> options) {
        int price = 0;

        ProductOption basicOption = options.stream()
                .filter(productOption -> productOption.getOptionType().equals(OptionType.BASIC.getValue()))
                .findFirst().orElse(null);

        if (basicOption != null) {
            price = basicOption.getPrice();
        }

        return price;
    }

    private int getTotalAddOptionPrice(List<ProductOption> options) {
        return options.stream()
                    .filter(productOption -> productOption.getOptionType().equals(OptionType.ADDITIONAL.getValue()))
                    .map(ProductOption::getPrice)
                    .mapToInt(Integer::intValue).sum();
    }
}
