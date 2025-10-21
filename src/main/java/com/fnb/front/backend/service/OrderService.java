package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.event.RequestCancelEvent;
import com.fnb.front.backend.controller.domain.event.RequestPaymentEvent;
import com.fnb.front.backend.controller.domain.response.OrderResponse;
import com.fnb.front.backend.controller.domain.validator.OrderValidator;
import com.fnb.front.backend.repository.*;
import com.fnb.front.backend.controller.domain.processor.OrderProcessor;
import com.fnb.front.backend.controller.dto.CreateOrderDto;
import com.fnb.front.backend.controller.dto.CreateOrderProductDto;
import com.fnb.front.backend.controller.domain.request.OrderCouponRequest;
import com.fnb.front.backend.controller.domain.request.OrderProductRequest;
import com.fnb.front.backend.controller.domain.request.OrderRequest;
import com.fnb.front.backend.util.OrderStatus;
import com.fnb.front.backend.util.OrderType;
import com.fnb.front.backend.util.Used;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;

    private final CouponRepository couponRepository;

    private final MemberRepository memberRepository;

    private final OrderRepository orderRepository;

    private final ApplicationEventPublisher requestCancelEvent;

    private final ApplicationEventPublisher ApprovePaymentEvent;

    @Transactional
    public OrderResponse create(OrderRequest orderRequest) {
        List<OrderProduct> orderProducts = new ArrayList<>();
        Order order                      = this.createOrder(orderRequest);
        Member member                    = this.createMember(orderRequest);
        List<Product> product            = this.createOrderProduct(orderRequest);
        List<Coupon> coupons             = this.createOrderCoupon(orderRequest);
        OrderProcessor orderProcessor    = new OrderProcessor(member, order, product, coupons, new OrderValidator());
        CreateOrderDto createOrderDto    = orderProcessor.buildOrder();

        order.setOrderId(createOrderDto.getOrderId());
        order.setOrderStatus(OrderStatus.TEMP.getValue());
        order.setOrderType(orderRequest.getOrderType() == 0 ? OrderType.PICKUP.getValue() : OrderType.DELIVERY.getValue());
        order.setDiscountAmount(BigDecimal.valueOf(createOrderDto.getDiscountAmount()));
        order.setCouponAmount(createOrderDto.getCouponAmount());
        order.setTotalAmount(BigDecimal.valueOf(createOrderDto.getOrderAmount()));

        this.insertOrder(order);

        for(CreateOrderProductDto element : createOrderDto.getOrderProducts()) {
            orderProducts.add(OrderProduct.builder()
                    .productId(element.getProductId())
                    .quantity(element.getQuantity())
                    .couponAmount(BigDecimal.valueOf(element.getCouponPrice()))
                    .couponId(element.getCouponId())
                    .paymentAmount(BigDecimal.valueOf(element.getOriginPrice()))
                    .discountAmount(BigDecimal.valueOf(element.getDiscountPrice()))
                    .orderId(element.getOrderId())
                    .build());
        }

        this.insertOrderProducts(orderProducts);

        if (this.isNonExecutePaymentGateWay(createOrderDto.getOrderProducts())) {
            this.ApprovePaymentEvent.publishEvent(RequestPaymentEvent.builder()
                    .order(this.findOrder(createOrderDto.getOrderId())));
        }

        return OrderResponse.builder()
                .orderId(createOrderDto.getOrderId())
                .memberName(member.getName())
                .productName("PRD_"+createOrderDto.getOrderId())
                .quantity(1)
                .purchasePrice(BigDecimal.valueOf(createOrderDto.getOrderAmount()))
                .vatAmount(BigDecimal.valueOf(createOrderDto.getOrderAmount()).divide(BigDecimal.valueOf(1.1), RoundingMode.HALF_EVEN))
                .taxAmount(BigDecimal.valueOf(0))
                .isNonPayment(this.isNonExecutePaymentGateWay(createOrderDto.getOrderProducts()))
                .build();
    }

    public void cancel(String orderId) {
        this.requestCancelEvent.publishEvent(RequestCancelEvent.builder()
                .orderId(orderId).build());
    }

    public Order findOrder(String orderId) {
        return this.orderRepository.findOrder(orderId);
    }

    private boolean isNonExecutePaymentGateWay(List<CreateOrderProductDto> orderProductRequests) {
        return orderProductRequests.stream()
                .map(CreateOrderProductDto::getPurchasePrice)
                .mapToInt(Integer::intValue).sum() == BigDecimal.ZERO.intValue();
    }

    private Order createOrder(OrderRequest orderRequest) {
        return Order.builder()
                .orderType(orderRequest.getOrderType())
                .usePoint(orderRequest.getPoint())
                .orderDate(LocalDateTime.now())
                .build();
    }

    private List<Product> createOrderProduct(OrderRequest orderRequest) {
        List<Product> orderProducts         = new ArrayList<>();
        List<Integer> reqProductIds         = orderRequest.getOrderProductRequests()
                .stream().map(OrderProductRequest::getProductId).toList();
        List<List<Integer>> optionIdsArray  = orderRequest.getOrderProductRequests()
                .stream().map(OrderProductRequest::getProductOptionIds).toList();
        List<Integer> optionIds             = optionIdsArray.stream().flatMap(List::stream).toList();
        List<Product> products              = this.productRepository.findProducts(reqProductIds, optionIds);
        List<Integer> existedProductIds     = products.stream().map(Product::getProductId).toList();

        if (!isEntireContained(existedProductIds, reqProductIds)) {
            return orderProducts;
        }

        for (OrderProductRequest element : orderRequest.getOrderProductRequests()) {
            for (Product product : products) {
                if(element.getProductId() == product.getProductId()) {
                    product.setQuantity(element.getQuantity());
                    orderProducts.add(product);
                }
            }
        }
        
        return orderProducts;
    }

    private List<Coupon> createOrderCoupon(OrderRequest orderRequest) {
        List<Integer> couponIds = orderRequest.getOrderCouponRequests().stream()
                .map(OrderCouponRequest::getCouponId)
                .toList();

        List<Coupon> coupons  = this.couponRepository.findCoupons(couponIds);

        for (Coupon coupon : coupons) {
            orderRequest.getOrderCouponRequests().stream()
                    .filter(orderCouponRequest -> Objects.equals(orderCouponRequest.getCouponId(), coupon.getCouponId()))
                    .findFirst()
                    .ifPresent(orderCouponRequest -> { orderCouponRequest.setCouponId(coupon.getCouponId()); });
        }

        return coupons;
    }

    private Member createMember(OrderRequest orderRequest) {
        Member member                       = this.memberRepository.findMember(orderRequest.getMemberId());
        List<MemberCoupon> memberCoupons    = this.memberRepository.findMemberCoupons(member.getMemberId(), Used.NOTUSED.getValue());
        member.setOwnedCoupon(memberCoupons);

        return member;
    }

    private void insertOrder(Order order) {
        this.orderRepository.insertOrder(order);
    }

    private void insertOrderProducts(List<OrderProduct> orderProducts) {
        this.orderRepository.insertOrderProducts(orderProducts);
    }

    public void updateStatus(String orderId, String orderStatus) {
        this.orderRepository.updateOrderStatus(orderId, orderStatus);
    }

    private boolean isEntireContained(List<Integer> origin, List<Integer> compare) {
        HashSet<Integer> onlyOneCompares = new HashSet<>(compare);
        HashSet<Integer> onlyOneOrigins  = new HashSet<>(origin);

        return onlyOneOrigins.containsAll(onlyOneCompares);
    }
}
