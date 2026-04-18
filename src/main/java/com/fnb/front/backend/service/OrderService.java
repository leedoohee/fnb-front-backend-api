package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.event.RequestCancelEvent;
import com.fnb.front.backend.controller.domain.event.RequestPaymentEvent;
import com.fnb.front.backend.controller.domain.response.OrderResponse;
import com.fnb.front.backend.controller.domain.validator.OrderValidator;
import com.fnb.front.backend.repository.*;
import com.fnb.front.backend.controller.domain.processor.OrderProcessor;
import com.fnb.front.backend.controller.domain.request.OrderCouponRequest;
import com.fnb.front.backend.controller.domain.request.OrderProductRequest;
import com.fnb.front.backend.controller.domain.request.OrderRequest;
import com.fnb.front.backend.util.Used;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductService productService;

    private final CouponService couponService;

    private final MemberService memberService;

    private final OrderRepository orderRepository;

    private final ApplicationEventPublisher requestCancelEvent;

    private final ApplicationEventPublisher processOrderEvent;

    @Transactional
    public OrderResponse create(OrderRequest orderRequest) {
        Order order                   = this.createOrder(orderRequest);
        Member member                 = this.createMember(orderRequest);
        List<Product> product         = this.createProduct(orderRequest.getOrderProductRequests());
        List<Coupon> coupons          = this.createOrderCoupon(orderRequest);
        List<ProductOption> options   = this.createOptions(orderRequest.getOrderProductRequests());
        OrderProcessor orderProcessor = new OrderProcessor(member, order, product, options, coupons, new OrderValidator());

        orderProcessor.buildOrder();

        this.insertOrder(order);
        this.insertOrderProducts(order.getOrderProducts());

        if (order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
            //결제 금액 0원이면
            this.processOrderEvent.publishEvent(RequestPaymentEvent.builder()
                    .order(this.findOrder(order.getOrderId())));
        }

        return this.makePaymentResponse(order);
    }

    public void cancel(String orderId) {
        this.requestCancelEvent.publishEvent(RequestCancelEvent.builder()
                .orderId(orderId).build());
    }

    public Order findOrder(String orderId) {
        return this.orderRepository.findOrder(orderId);
    }

    private Order createOrder(OrderRequest orderRequest) {
        return Order.builder()
                .orderType(orderRequest.getOrderType())
                .usePoint(orderRequest.getPoint())
                .orderDate(LocalDateTime.now())
                .build();
    }

    private List<ProductOption> createOptions(List<OrderProductRequest> orderProductRequests) {
        List<Integer> orderProductIds     = orderProductRequests.stream().map(OrderProductRequest::getProductId).toList();
        List<Integer> orderOptionIds      = orderProductRequests.stream()
                .flatMap(OrderProductRequest -> OrderProductRequest.getProductOptionIds().stream())
                .distinct().toList();

        return this.productService.findProductWithOptions(orderProductIds, orderOptionIds);
    }

    private List<Product> createProduct(List<OrderProductRequest> orderProductRequests) {
        List<Integer> orderProductIds = orderProductRequests.stream()
                .map(OrderProductRequest::getProductId)
                .toList();

        Map<Integer, Product> productMap = this.productService.findProducts(orderProductIds)
                .stream()
                .collect(Collectors.toMap(Product::getProductId, p -> p));

        return orderProductRequests.stream()
                .map(request -> {
                    Product product = productMap.get(request.getProductId());
                    product.prepareOrder(request.getProductOptionIds(), request.getQuantity());
                    return product;
                })
                .toList();
    }

    private List<Coupon> createOrderCoupon(OrderRequest orderRequest) {
        List<Integer> couponIds = orderRequest.getOrderCouponRequests().stream()
                .map(OrderCouponRequest::getCouponId)
                .toList();

        List<Coupon> coupons  = this.couponService.findCoupons(couponIds);

        for (Coupon coupon : coupons) {
            orderRequest.getOrderCouponRequests().stream()
                    .filter(orderCouponRequest -> Objects.equals(orderCouponRequest.getCouponId(), coupon.getCouponId()))
                    .findFirst()
                    .ifPresent(orderCouponRequest -> { orderCouponRequest.setCouponId(coupon.getCouponId()); });
        }

        return coupons;
    }

    private Member createMember(OrderRequest orderRequest) {
        Member member                       = this.memberService.findMember(orderRequest.getMemberId());
        List<MemberCoupon> memberCoupons    = this.memberService.findMemberCoupons(member.getMemberId(), Used.NOTUSED.getValue());
        member.setOwnedCoupon(memberCoupons);

        return member;
    }

    private OrderResponse makePaymentResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .memberName(order.getMember().getName())
                .productName("PRD_"+order.getOrderId())
                .quantity(1)
                .purchasePrice(order.getTotalAmount())
                .vatAmount(order.getTotalAmount().divide(BigDecimal.valueOf(1.1), RoundingMode.HALF_EVEN)) //TODO 부가세를 포함하지 않을 경우를 미계산하는 로직필요
                .taxAmount(BigDecimal.valueOf(0))
                .isNonPayment(order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0)
                .build();
    }

    protected void insertOrder(Order order) {
        this.orderRepository.insertOrder(order);
    }

    protected void insertOrderProducts(List<OrderProduct> orderProducts) {
        this.orderRepository.insertOrderProducts(orderProducts);
    }

    public void updateStatus(String orderId, String orderStatus) {
        this.orderRepository.updateOrderStatus(orderId, orderStatus);
    }
}
