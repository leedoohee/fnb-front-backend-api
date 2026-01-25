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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

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
                                                ? null : this.buildOrderProduct(orderRequest.getOrderProductRequests());
        List<Coupon> coupons             = this.buildOrderCoupon(orderRequest);
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

        if (this.isZeroPayment(createOrderDto.getOrderProducts())) {
            this.processOrderEvent.publishEvent(RequestPaymentEvent.builder()
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
                .isNonPayment(this.isZeroPayment(createOrderDto.getOrderProducts()))
                .build();
    }

    public void cancel(String orderId) {
        this.requestCancelEvent.publishEvent(RequestCancelEvent.builder()
                .orderId(orderId).build());
    }

    public Order findOrder(String orderId) {
        return this.orderRepository.findOrder(orderId);
    }

    private boolean isZeroPayment(List<CreateOrderProductDto> orderProductRequests) {
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

    private boolean isExistedNonSellProducts(List<OrderProductRequest> orderProductRequests) {
        List<Integer> orderProductIds       = orderProductRequests.stream().map(OrderProductRequest::getProductId).toList();
        List<Integer> orderOptionIds        = orderProductRequests.stream()
                                                .flatMap(OrderProductRequest -> OrderProductRequest.getProductOptionIds().stream())
                                                .toList();
        List<Product> aliveProducts         = this.productService.findProductWithOptions(orderProductIds, orderOptionIds);
        List<Integer> aliveProductIds       = aliveProducts.stream().map(Product::getProductId).toList();

        if (!this.isEntireContained(aliveProductIds, orderProductIds)) {
            return true;
        }

        if (orderOptionIds.size() > aliveProducts.size()) {
            return true;
        }

        return false;
    }

    private List<Product> buildOrderProduct(List<OrderProductRequest> orderProductRequests) {
        List<Product> orderProducts   = new ArrayList<>();
        List<Integer> orderProductIds = orderProductRequests.stream().map(OrderProductRequest::getProductId).toList();
        List<Product> products        = this.productService.findProducts(orderProductIds);

        for (OrderProductRequest orderProduct : orderProductRequests) {
            Product product = products.stream()
                                .filter(p -> p.getProductId() == orderProduct.getProductId())
                                .findFirst().orElse(null);

            if(product != null) {
                product.setQuantity(orderProduct.getQuantity());
                product.setProductOption(product.getProductOption());
                orderProducts.add(product);
            }
        }

        return orderProducts;
    }

    private List<Coupon> buildOrderCoupon(OrderRequest orderRequest) {
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

    private boolean isEntireContained(List<Integer> aliveProducts, List<Integer> orderProducts) {
        HashSet<Integer> onlyOneAliveSet  = new HashSet<>(aliveProducts);
        HashSet<Integer> onlyOneOrderSet = new HashSet<>(orderProducts);

        return onlyOneAliveSet.containsAll(onlyOneOrderSet);
    }
}
