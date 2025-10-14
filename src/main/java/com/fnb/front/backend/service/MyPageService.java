package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.request.MyPageRequest;
import com.fnb.front.backend.controller.domain.response.*;
import com.fnb.front.backend.repository.MemberRepository;
import com.fnb.front.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final OrderRepository orderRepository;

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public PageResponse<MyOrderResponse> getMyOrders(MyPageRequest myPageRequest) {
        long totalCount                                 = this.orderRepository.findTotalOrderCount(myPageRequest);
        int lastPageNumber                              = (int) (Math.ceil((double) totalCount / myPageRequest.getPageLimit()));
        List<Order> orders                              = this.orderRepository.findOrders(myPageRequest);
        List<MyOrderResponse>   orderResponses          = new ArrayList<>();

        for (Order order : orders) {
            List<MyOrderProductResponse> orderProductResponse = new ArrayList<>();;
            List<OrderProduct> products = order.getOrderProducts();

            for (OrderProduct product : products) {
                List<MyOrderOptionResponse> orderOptionResponses = new ArrayList<>();
                List<OrderOption> option = product.getOrderOptions();

                for (OrderOption orderOption : option) {
                    orderOptionResponses.add(MyOrderOptionResponse.builder()
                                                .optionId(orderOption.getOptionId())
                                                .optionName(orderOption.getOptionName())
                                                .price(orderOption.getPrice())
                                                .build());
                }

                orderProductResponse.add(MyOrderProductResponse.builder()
                        .orderProductId(product.getOrderProductId())
                        .quantity(product.getQuantity())
                        .basicOptionName(product.getOptionName())
                        .additionalOptions(orderOptionResponses)
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

    //TODO 네이티브로?
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
