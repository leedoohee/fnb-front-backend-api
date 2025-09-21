package com.fnb.backend.Service;

import com.fnb.backend.controller.domain.*;
import com.fnb.backend.controller.domain.response.OrderResponse;
import com.fnb.backend.repository.CouponRepository;
import com.fnb.backend.repository.MemberRepository;
import com.fnb.backend.repository.ProductRepository;
import com.fnb.backend.controller.domain.processor.OrderProcessor;
import com.fnb.backend.controller.dto.CreateOrderDto;
import com.fnb.backend.controller.dto.CreateOrderProductDto;
import com.fnb.backend.controller.request.Order.OrderCouponRequest;
import com.fnb.backend.controller.request.Order.OrderProductRequest;
import com.fnb.backend.controller.request.Order.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public OrderResponse process(OrderRequest orderRequest) {
        Order order                 = this.createOrder(orderRequest);
        List<Product> orderProducts = this.createOrderProduct(orderRequest);
        List<Coupon> orderCoupons   = this.createOrderCoupon(orderRequest);
        Member member               = this.createMember(orderRequest);

        OrderProcessor orderProcessor = new OrderProcessor(member, order, orderProducts, orderCoupons);
        CreateOrderDto createOrderDto = orderProcessor.buildOrder();

        if(this.isNonExecutePaymentGateWay(createOrderDto.getOrderProducts())) {
            //payment 저장
            //주문확정 업데이트
        } else {
            //주문정보 임시 저장
            //
        }

        return OrderResponse
    }

    private boolean isNonExecutePaymentGateWay(List<CreateOrderProductDto> orderProductRequests) {
        return orderProductRequests.stream()
                .map(CreateOrderProductDto::getPurchasePrice)
                .mapToInt(Integer::intValue).sum() == BigDecimal.ZERO.intValue();
    }

    private Order createOrder(OrderRequest orderRequest) {
        return Order.builder()
                .orderType(orderRequest.getOrderType())
                .orderStatus("0")
                .point(orderRequest.getPoint())
                .orderDate(new Date())
                .merchantId(orderRequest.getMerchantId())
                .build();
    }

    private List<Product> createOrderProduct(OrderRequest orderRequest) {
        List<Product> orderProducts = new ArrayList<>();

        for (OrderProductRequest orderProductRequest : orderRequest.getOrderProductRequests()) {
            Product product = productRepository.find(orderProductRequest.getProductId());

            product.setProductOptions(productRepository.findOptionsById(product.getId(), orderProductRequest.getProductOptionId()));
            product.setPurchaseQuantity(orderProductRequest.getQuantity());
            orderProducts.add(product);
        }

        return orderProducts;
    }

    private List<Coupon> createOrderCoupon(OrderRequest orderRequest) {
        List<Integer> couponIds = orderRequest.getOrderCouponRequests().stream()
                            .map(OrderCouponRequest::getCouponId)
                            .toList();

        List<Coupon> coupons  = couponRepository.findInIds(couponIds.toString());

        for (Coupon coupon : coupons) {
            orderRequest.getOrderCouponRequests().stream()
                    .filter(orderCouponRequest -> Objects.equals(orderCouponRequest.getCouponId(), coupon.getId()))
                    .findFirst()
                    .ifPresent(orderCouponRequest -> { orderCouponRequest.setCouponId(coupon.getId()); });
        }

        return coupons;
    }

    private Member createMember(OrderRequest orderRequest) {
        Member member                       = memberRepository.find(orderRequest.getMemberId());
        List<MemberCoupon> memberCoupons    = memberRepository.findMemberCouponsById(member.getId());
        List<Point> points                  = memberRepository.findPointsById(member.getId());

        member.setPoints(points);
        member.setOwnedCoupon(memberCoupons);

        return member;
    }
}
