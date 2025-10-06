package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.Member;
import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.OrderProduct;
import com.fnb.front.backend.repository.MemberRepository;
import com.fnb.front.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MyPageService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    public void getOrders(int memberId, String startDate, String endDate, int page, int pageLimit) {
        List<Order> orders = this.orderRepository.getOrders(memberId, startDate, endDate, page, pageLimit);

        //구체적 리턴 데이터 정하기
        //n+1 포인트 개선 필요 in 절 조회 혹은 fetch join
        for (Order order : orders) {
            int orderProductCount = this.orderRepository.getOrderProducts(order.getOrderId()).size();
        }
    }

    public void getMyInfo(int memberId) {
        Member member = this.memberRepository.find(memberId);
        member.getOwnedPoint();
        member.getOwnedCouponCount();
        // 포인트, 쿠폰수조회
    }
}
