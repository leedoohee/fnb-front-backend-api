package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.response.OrderResponse;
import com.fnb.front.backend.repository.*;
import com.fnb.front.backend.controller.domain.processor.OrderProcessor;
import com.fnb.front.backend.controller.dto.CreateOrderDto;
import com.fnb.front.backend.controller.dto.CreateOrderProductDto;
import com.fnb.front.backend.controller.domain.request.order.OrderCouponRequest;
import com.fnb.front.backend.controller.domain.request.order.OrderProductRequest;
import com.fnb.front.backend.controller.domain.request.order.OrderRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    private final ProductRepository productRepository;

    private final CouponRepository couponRepository;

    private final MemberRepository memberRepository;

    private final OrderRepository orderRepository;

    private final PaymentService paymentService;

    public OrderService(ProductRepository productRepository,
                        CouponRepository couponRepository,
                        MemberRepository memberRepository,
                        OrderRepository orderRepository, PaymentService paymentService) {

        this.productRepository = productRepository;
        this.couponRepository = couponRepository;
        this.memberRepository = memberRepository;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public OrderResponse create(OrderRequest orderRequest) throws Exception {
        Order order                         = this.createOrder(orderRequest);
        List<Product> orderProducts         = this.createOrderProduct(orderRequest);
        List<Coupon> orderCoupons           = this.createOrderCoupon(orderRequest);
        Member member                       = this.createMember(orderRequest);
        List<OrderProduct> newOrderProducts = new ArrayList<>();
        OrderProcessor orderProcessor       = new OrderProcessor(member, order, orderProducts, orderCoupons);
        CreateOrderDto createOrderDto       = orderProcessor.buildOrder();

        Order newOrder = Order.builder()
                .orderId(createOrderDto.getOrderId())
                .orderDate(createOrderDto.getOrderDate())
                .orderStatus("0")
                .orderType(1)
                .discountAmount(createOrderDto.getDiscountAmount())
                .couponAmount(createOrderDto.getCouponAmount())
                .totalAmount(createOrderDto.getOrderAmount())
                .build();

        this.insertOrder(newOrder);

        for(CreateOrderProductDto element : createOrderDto.getOrderProducts()) {
            newOrderProducts.add(OrderProduct.builder()
                    .orderProductId(Integer.parseInt(element.getOrderProductId()))
                    .productId(element.getProductId())
                    .quantity(element.getQuantity())
                    .couponAmount(BigDecimal.valueOf(element.getCouponPrice()))
                    .couponId(element.getCouponId())
                    .paymentAmount(BigDecimal.valueOf(element.getOriginPrice()))
                    .discountAmount(BigDecimal.valueOf(element.getDiscountPrice()))
                    .orderId(element.getOrderId())
                    .build());
        }

        this.insertOrderProducts(newOrderProducts);

        if(this.isNonExecutePaymentGateWay(createOrderDto.getOrderProducts())) {
            this.paymentService.insertPayments(createOrderDto.getOrderId(), null);
        }

        return OrderResponse.builder()
                .orderId(createOrderDto.getOrderId())
                .memberName(member.getName())
                .productName("PRD_"+createOrderDto.getOrderId())
                .quantity(1)
                .purchasePrice(createOrderDto.getOrderAmount())
                .vatAmount(createOrderDto.getOrderAmount().multiply(BigDecimal.valueOf(1.1)))
                .taxAmount(BigDecimal.valueOf(0))
                .isNonPayment(this.isNonExecutePaymentGateWay(createOrderDto.getOrderProducts()))
                .build();
    }

    public Order getOrder(String orderId) {
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
        List<Integer> productIdList         = orderRequest.getOrderProductRequests().stream().map(OrderProductRequest::getProductId).toList();
        List<List<Integer>> optionIdsArray  = orderRequest.getOrderProductRequests().stream().map(OrderProductRequest::getProductOptionId).toList();
        List<Integer> optionIdList          = optionIdsArray.stream().flatMap(List::stream).toList();
        List<Product> products              = this.productRepository.findProducts(productIdList);
        List<ProductOption> options         = this.productRepository.findOptions(optionIdList);

        for (Product product : products) {
            product.setProductOption(options.stream().filter(productOption -> productOption.getProductId() == product.getId()).toList());
            product.setQuantity(Objects.requireNonNull(orderRequest.getOrderProductRequests().stream()
                    .filter(orderProductRequest -> orderProductRequest.getProductId() == product.getId())
                    .findFirst().orElse(null)).getQuantity());

            orderProducts.add(product);
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
                    .filter(orderCouponRequest -> Objects.equals(orderCouponRequest.getCouponId(), coupon.getId()))
                    .findFirst()
                    .ifPresent(orderCouponRequest -> { orderCouponRequest.setCouponId(coupon.getId()); });
        }

        return coupons;
    }

    private Member createMember(OrderRequest orderRequest) {
        Member member                       = this.memberRepository.findMember(orderRequest.getMemberId());
        List<MemberCoupon> memberCoupons    = this.memberRepository.findMemberCoupons(member.getMemberId());
        member.setOwnedCoupon(memberCoupons);

        return member;
    }

    private void insertOrder(Order order) {
        this.orderRepository.insertOrder(order);
    }

    private void insertOrderProducts(List<OrderProduct> orderProducts) {
        this.orderRepository.insertOrderProducts(orderProducts);
    }
}
