package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.request.MyPageRequest;
import com.fnb.front.backend.controller.domain.response.*;
import com.fnb.front.backend.controller.dto.MemberAggregatesDto;
import com.fnb.front.backend.repository.MemberRepository;
import com.fnb.front.backend.repository.OrderRepository;
import com.fnb.front.backend.util.Used;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final OrderRepository orderRepository;

    private final MemberRepository memberRepository;
    //TODO 데이터 구조 다시 정하고 재개발 예정
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

    public MyInfoResponse getMyInfo(String memberId) {
        Member member                    = this.memberRepository.findMember(memberId);
        MemberAggregatesDto aggregates   = this.memberRepository.getMemberAggregates(memberId, Used.NOTUSED.getValue());

        return MyInfoResponse.builder()
                .address(member.getAddress())
                .memberName(member.getName())
                .ownedCouponCount(aggregates.getCouponCount())
                .ownedPoint(aggregates.getPoint())
                .orderCount(aggregates.getOrderCount())
                .build();
    }
}
