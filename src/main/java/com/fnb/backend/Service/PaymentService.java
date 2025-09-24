package com.fnb.backend.Service;

import com.fnb.backend.controller.domain.Order;
import com.fnb.backend.controller.domain.OrderProduct;
import com.fnb.backend.controller.domain.PayFactory;
import com.fnb.backend.controller.domain.event.OrderResultEvent;
import com.fnb.backend.controller.domain.processor.PaymentProcessor;
import com.fnb.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.backend.controller.domain.response.PaymentResultResponse;
import com.fnb.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.backend.controller.dto.ApprovePaymentDto;
import com.fnb.backend.controller.domain.request.Payment.RequestPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private OrderService orderService;

    public PaymentService(OrderService orderService) {
        this.orderService = orderService;
    }

    public RequestPaymentResponse request(RequestPayment requestPayment) {
        PaymentProcessor paymentProcessor = new PaymentProcessor(PayFactory.getPay(requestPayment.getPayType()));
        return paymentProcessor.request(requestPayment);
    }

    @Transactional
    public PaymentResultResponse approveKakaoResult(ApprovePaymentDto approvePaymentDto) {
        PaymentProcessor paymentProcessor   = new PaymentProcessor(PayFactory.getPay("K"));
        ApprovePaymentResponse response     = paymentProcessor.approve(approvePaymentDto);

        boolean result = this.insertPayments(response.getOrderId(), response);

        return new PaymentResultResponse();
    }

    @Transactional
    public boolean insertPayments(String orderId, ApprovePaymentResponse approvePaymentResponse) {
        List<OrderProduct> orderProducts    = this.orderService.getOrderProducts(orderId);
        Order order                         = this.orderService.getOrder(orderId);

        int couponAmount = orderProducts.stream().map(OrderProduct::getCouponPrice).mapToInt(Integer::intValue).sum();
        int pointAmount  = order.getUsePoint().intValue();

        //금액 비교 로직

        //payment master 추가
        // 포인트, 쿠폰 사용 디테일 추가

        if(approvePaymentResponse != null) {
            //pg 결제내역추가
            int paymentAmount       = approvePaymentResponse.getTotalAmount().intValue();
            String paymentMethod    = approvePaymentResponse.getPaymentMethod();
        }

        OrderResultEvent event = OrderResultEvent.builder()
                .member(this.orderService.getMember(order.getMemberId()))
                .order(order)
                .orderProducts(orderProducts)
                .build();

        this.generateEvent(event);

        return true;
    }

    private void generateEvent(OrderResultEvent event) {
        //이벤트 패턴으로 재고 차감, 알림톡,
        eventPublisher.publishEvent(event);
    }
}
