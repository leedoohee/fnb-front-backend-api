package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.event.OrderResultEvent;
import com.fnb.front.backend.controller.domain.processor.PaymentProcessor;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.PaymentResultResponse;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.dto.ApprovePaymentDto;
import com.fnb.front.backend.controller.domain.request.Payment.RequestPayment;
import com.fnb.front.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class PaymentService {

    private final ApplicationEventPublisher eventPublisher;

    private final PaymentRepository paymentRepository;

    private final OrderService orderService;

    public PaymentService(ApplicationEventPublisher eventPublisher, PaymentRepository paymentRepository, OrderService orderService) {
        this.eventPublisher = eventPublisher;
        this.paymentRepository = paymentRepository;
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

        int couponAmount = orderProducts.stream().map(OrderProduct::getCouponAmount).mapToInt(BigDecimal::intValue).sum();
        int pointAmount  = order.getUsePoint().intValue();

        //금액 비교 로직

        //payment master 추가
        this.paymentRepository.insertPayment(Payment.builder()
                .paymentAt(LocalDateTime.now())
                .totalAmount(order.getTotalAmount())
                .orderId(order.getOrderId())
                .build());

        if (couponAmount > 0) {
            this.paymentRepository.insertPaymentElement(PaymentElement.builder()
                    .paymentMethod("COUPON")
                    .amount(BigDecimal.valueOf(couponAmount))
                    .build());
        }

        if (pointAmount > 0) {
           this.paymentRepository.insertPaymentElement(PaymentElement.builder()
                   .paymentMethod("POINT")
                   .amount(BigDecimal.valueOf(pointAmount))
                   .build());
        }

        // 포인트, 쿠폰 사용 디테일 추가

        if(approvePaymentResponse != null) {
            //pg 결제내역추가
            int paymentAmount       = approvePaymentResponse.getTotalAmount().intValue();
            String paymentMethod    = approvePaymentResponse.getPaymentMethod();

            this.paymentRepository.insertPaymentElement(PaymentElement.builder()
                    .paymentMethod(paymentMethod)
                    .amount(BigDecimal.valueOf(paymentAmount))
                    .build());
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
