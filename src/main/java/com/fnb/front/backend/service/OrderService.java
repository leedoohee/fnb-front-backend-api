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
import org.springframework.transaction.annotation.Propagation;
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

    @Transactional(readOnly = true)
    public OrderResponse create(OrderRequest orderRequest) {
        List<OrderProduct> orderProducts = new ArrayList<>();
        Order order                      = this.createOrder(orderRequest);
        Member member                    = this.createMember(orderRequest);
        List<Product> product            = this.isExistedNonSellProducts(orderRequest.getOrderProductRequests())
                                                ? null : this.createProduct(orderRequest.getOrderProductRequests());
        List<Coupon> coupons             = this.createOrderCoupon(orderRequest);
        OrderProcessor orderProcessor    = new OrderProcessor(member, order, product, coupons, new OrderValidator());

        orderProcessor.buildOrder();

        this.insertOrder(order);
        this.insertOrderProducts(orderProducts);

        if (this.isZeroPayment(order.getOrderProducts())) {
            this.processOrderEvent.publishEvent(RequestPaymentEvent.builder()
                    .order(this.findOrder(order.getOrderId())));
        }

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .memberName(member.getName())
                .productName("PRD_"+order.getOrderId())
                .quantity(1)
                .purchasePrice(order.getTotalAmount())
                .vatAmount(order.getTotalAmount().divide(BigDecimal.valueOf(1.1), RoundingMode.HALF_EVEN))
                .taxAmount(BigDecimal.valueOf(0))
                .isNonPayment(this.isZeroPayment(order.getOrderProducts()))
                .build();
    }

    public void cancel(String orderId) {
        this.requestCancelEvent.publishEvent(RequestCancelEvent.builder()
                .orderId(orderId).build());
    }

    public Order findOrder(String orderId) {
        return this.orderRepository.findOrder(orderId);
    }

    private boolean isZeroPayment(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map(orderProduct -> orderProduct.getPaymentAmount().intValue())
                .mapToInt(Integer::intValue).sum() == BigDecimal.ZERO.intValue();
    }

    private Order createOrder(OrderRequest orderRequest) {
        return Order.builder()
                .orderType(orderRequest.getOrderType())
                .usePoint(orderRequest.getPoint())
                .orderDate(LocalDateTime.now())
                .build();
    }

    private boolean isExistedNonSellProducts(List<OrderProductRequest> orderProductRequests) {
        List<Integer> orderProductIds     = orderProductRequests.stream().map(OrderProductRequest::getProductId).toList();
        List<Integer> orderOptionIds      = orderProductRequests.stream()
                                                .flatMap(OrderProductRequest -> OrderProductRequest.getProductOptionIds().stream())
                                                .distinct().toList();
        List<ProductOption> aliveOptions  = this.productService.findProductWithOptions(orderProductIds, orderOptionIds);
        List<Integer> aliveProductIds     = aliveOptions.stream().map(ProductOption::getProductId).distinct().toList();
        List<Integer> aliveOptionIds      = aliveOptions.stream().map(ProductOption::getProductOptionId).distinct().toList();
        HashSet<Integer> aliveSet         = new HashSet<>(aliveProductIds);
        HashSet<Integer> orderSet         = new HashSet<>(orderProductIds);

        if (!aliveSet.containsAll(orderSet)) {
            return true;
        }

        return orderOptionIds.size() > aliveOptionIds.size();
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
                    product.setQuantity(request.getQuantity());
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void insertOrder(Order order) {
        this.orderRepository.insertOrder(order);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void insertOrderProducts(List<OrderProduct> orderProducts) {
        this.orderRepository.insertOrderProducts(orderProducts);
    }

    public void updateStatus(String orderId, String orderStatus) {
        this.orderRepository.updateOrderStatus(orderId, orderStatus);
    }
}
