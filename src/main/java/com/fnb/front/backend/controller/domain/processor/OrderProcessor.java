package com.fnb.front.backend.controller.domain.processor;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.implement.DiscountPolicy;
import com.fnb.front.backend.controller.domain.implement.Calculator;
import com.fnb.front.backend.controller.domain.validator.OrderValidator;
import com.fnb.front.backend.controller.dto.CreateOrderProductDto;
import com.fnb.front.backend.util.CommonUtil;
import com.fnb.front.backend.util.OptionType;
import com.fnb.front.backend.util.OrderStatus;
import com.fnb.front.backend.util.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class OrderProcessor {
    private final Member member;
    private final Order order;
    private final List<Product> products;
    private final List<Coupon> coupons;
    private final List<ProductOption> aliveOptions;
    private final OrderValidator orderValidator;

    public OrderProcessor(Member member, Order order, List<Product> products, List<ProductOption> options, List<Coupon> coupons, OrderValidator orderValidator) {
        this.member = member;
        this.order = order;
        this.products = products;
        this.coupons = coupons;
        this.aliveOptions = options;
        this.orderValidator = orderValidator;
    }

    public void buildOrder() {

        if (!this.orderValidator.isCanPurchaseMember(this.member)) {
            throw new IllegalStateException("구매 불가능한 회원입니다.");
        }

        if (!this.orderValidator.isCanUsePoint(this.order.getUsePoint(), this.member.getPoints())) {
            // 동시적 요청으로 인해 존재가 확인되지 않음은 exception 처리
            throw new IllegalStateException("사용 가능한 포인트를 초과하였습니다.");
        }

        if (!this.orderValidator.isOwnedCoupons(this.member, this.coupons)) {
            // 동시적 요청으로 인해 존재가 확인되지 않음은 exception 처리
            throw new IllegalStateException("소유하지 않은 쿠폰을 사용하였습니다.");
        }

        if (!this.orderValidator.isCanUseCoupons(this.coupons, this.member)) {
            // 동시적 요청으로 인해 존재가 확인되지 않음은 exception 처리
            throw new IllegalStateException("사용 불가능한 쿠폰이 포함되어 있습니다.");
        }

        boolean productResult = this.orderValidator.isCanOrderProducts(this.products, this.aliveOptions);

        assert productResult : "구매 불가능한 상품이 포함되어 있습니다.";

        String orderId = CommonUtil.generateOrderId();
        List<CreateOrderProductDto> orderProductsDto = this.buildOrderProducts(orderId, this.member, this.products, this.coupons);
        int totalCouponPrice        = orderProductsDto.stream().mapToInt(CreateOrderProductDto::getCouponPrice).sum();
        int totalMemberShipPrice    = orderProductsDto.stream().mapToInt(CreateOrderProductDto::getMemberShipPrice).sum();
        int totalOriginPrice        = orderProductsDto.stream().mapToInt(CreateOrderProductDto::getOriginPrice).sum();

        this.order.setOrderId(orderId);
        this.order.setOrderStatus(OrderStatus.TEMP.getValue());
        this.order.setOrderType(order.getOrderType() == 0 ? OrderType.PICKUP.getValue() : OrderType.DELIVERY.getValue());
        this.order.setDiscountAmount(BigDecimal.valueOf(totalCouponPrice + totalMemberShipPrice + this.order.getUsePoint().intValue()));
        this.order.setCouponAmount(totalCouponPrice);
        this.order.setTotalAmount(BigDecimal.valueOf(totalOriginPrice));
        this.order.setOrderDate(LocalDateTime.now());
        this.order.setMemberName(this.member.getName());

        for(CreateOrderProductDto element : orderProductsDto) {
            this.order.getOrderProducts().add(OrderProduct.builder()
                    .productId(element.getProductId())
                    .quantity(element.getQuantity())
                    .couponAmount(BigDecimal.valueOf(element.getCouponPrice()))
                    .couponId(element.getCouponId())
                    .paymentAmount(BigDecimal.valueOf(element.getOriginPrice()))
                    .discountAmount(BigDecimal.valueOf(element.getDiscountPrice()))
                    .orderId(element.getOrderId())
                    .build());
        }
    }

    private List<CreateOrderProductDto> buildOrderProducts(String orderId, Member member, List<Product> products, List<Coupon> coupons) {
        List<CreateOrderProductDto> createOrderProductsDto = new ArrayList<>();

        for (Product product : products) {
            int memberShipPrice = 0;
            int basicOptionWithPrice = product.getPrice().intValue() + product.getBasicOptionPrice();

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
                                    + this.calcTotalAddOptionPrice(product.getProductOption()))
                    .couponPrice(couponPrice)
                    .discountPrice(couponPrice + memberShipPrice)
                    .build();

            createOrderProductsDto.add(orderProduct);
        }

        return createOrderProductsDto;
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
                DiscountPolicy couponPolicy = DiscountFactory.getPolicy(coupon.getDiscountType());

                int priceWithQuantity = this.calcPriceWithQuantity(product.getPrice().intValue(), product.getQuantity());
                CouponCalculator couponPriceCalculator = new CouponCalculator(coupon, priceWithQuantity, couponPolicy);

                return couponPriceCalculator.calculate();

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

    private int calcPriceWithQuantity(int price, int quantity) {
        return price * quantity;
    }

    public int calcTotalAddOptionPrice(List<ProductOption> productOptions) {
        return productOptions.stream()
                .filter(productOption -> productOption.getOptionType().equals(OptionType.ADDITIONAL.getValue()))
                .map(ProductOption::getPrice)
                .mapToInt(Integer::intValue).sum();
    }
}
