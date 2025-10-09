package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.request.MyPageRequest;
import com.fnb.front.backend.controller.domain.response.*;
import com.fnb.front.backend.repository.MemberRepository;
import com.fnb.front.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MyPageService {

    private final OrderRepository orderRepository;

    private final MemberRepository memberRepository;

    public MyPageService(OrderRepository orderRepository, MemberRepository memberRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<MyOrderResponse> getMyOrders(MyPageRequest myPageRequest) {
        long totalCount                                 = this.orderRepository.findTotalOrderCount(myPageRequest);
        int lastPageNumber                              = (int) (Math.ceil((double) totalCount / myPageRequest.getPageLimit()));
        List<Order> orders                              = this.orderRepository.findOrders(myPageRequest);
        List<String> orderIdList                        = orders.stream().map(Order::getOrderId).toList();
        List<OrderProduct> orderProducts                = this.orderRepository.findOrderProducts(orderIdList);
        List<Integer> orderProductIdList                = orderProducts.stream().map(OrderProduct::getOrderProductId).toList();
        List<OrderAdditionalOption>  additionalOptions  = this.orderRepository.findOrderAdditionalOptions(orderProductIdList);
        List<MyOrderResponse>   orderResponses          = new ArrayList<>();

        for (Order order : orders) {
            List<MyOrderProductResponse> orderProductResponse = new ArrayList<>();;
            List<OrderProduct> products = orderProducts.stream()
                                .filter(orderProduct -> orderProduct.getOrderId().equals(order.getOrderId()))
                                .toList();

            for (OrderProduct product : products) {
                List<MyAdditionalOptionResponse> additionalOptionResponse = new ArrayList<>();
                List<OrderAdditionalOption> option = additionalOptions.stream()
                        .filter(orderAdditionalOption ->
                                    orderAdditionalOption.getOrderProductId() == product.getOrderProductId())
                        .toList();

                for (OrderAdditionalOption additionalOption : option) {
                    additionalOptionResponse.add(MyAdditionalOptionResponse.builder()
                                                .optionId(additionalOption.getAdditionalOptionId())
                                                .optionName(additionalOption.getAdditionalOptionName())
                                                .price(additionalOption.getPrice())
                                                    .build());
                }

                orderProductResponse.add(MyOrderProductResponse.builder()
                        .orderProductId(product.getOrderProductId())
                        .quantity(product.getQuantity())
                        .basicOptionId(product.getOptionId())
                        .basicOptionName(product.getOptionName())
                        .additionalOptions(additionalOptionResponse)
                        .build());
            }

            orderResponses.add(MyOrderResponse.builder()
                    .orderId(order.getOrderId())
                    .memberName(order.getMemberName())
                    .totalAmount(order.getTotalAmount())
                    .orderProducts(orderProductResponse).build());
        }

        return PageResponse.<MyOrderResponse>builder()
                .lastPage(lastPageNumber)
                .data(orderResponses).build();
    }

    @Transactional(readOnly = true)
    public MyInfoResponse getMyInfo(String memberId) {
        Member member                    = this.memberRepository.findMember(memberId);
        List<MemberCoupon> memberCoupons = this.memberRepository.findMemberCoupons(memberId);
        List<MemberPoint> memberPoints   = this.memberRepository.findMemberPoints(memberId);

        int ownedCouponCount = memberCoupons.stream()
                                    .filter(memberCoupon -> memberCoupon.getIsUsed().equals("1"))
                                    .map(MemberCoupon::getCouponId)
                                    .mapToInt(Integer::intValue).sum();

        int ownedPoint       = memberPoints.stream()
                                    .filter(memberPoint -> memberPoint.getIsUsed().equals("1"))
                                    .map(MemberPoint::getId)
                                    .mapToInt(Integer::intValue).sum();

        return MyInfoResponse.builder()
                .address(member.getAddress())
                .memberName(member.getName())
                .ownedCouponCount(ownedCouponCount)
                .ownedPoint(ownedPoint)
                .build();
    }
}
