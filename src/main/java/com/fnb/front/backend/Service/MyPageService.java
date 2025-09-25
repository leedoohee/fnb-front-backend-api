package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.Order;
import com.fnb.front.backend.controller.domain.OrderProduct;
import com.fnb.front.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MyPageService {

    @Autowired
    private OrderRepository orderRepository;

    public void getOrders(int memberId, String startDate, String endDate, int page, int pageLimit) {
        List<Order> orders = this.orderRepository.getOrders(memberId, startDate, endDate, page, pageLimit);

        //구체적 리턴 데이터 정하기
        for (Order order : orders) {
            int orderProductCount = this.orderRepository.getOrderProducts(order.getOrderId()).size();
        }
    }
}
