package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.event.OrderStatusUpdateEvent;
import com.fnb.front.backend.controller.domain.event.PaymentCancelEvent;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.dto.CancelPayDto;
import com.fnb.front.backend.repository.*;
import com.fnb.front.backend.util.*;
import com.fnb.front.backend.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AfterPaymentService {
    private final PaymentRepository paymentRepository;

    private final ProductService productService;

    private final CouponService couponService;

    private final PointService pointService;

    private final ApplicationEventPublisher paymentCancelEvent;

    private final ApplicationEventPublisher orderStatusUpdateEvent;

    @Transactional
    public void callPaymentProcess(Order order, ApprovePaymentResponse approvePaymentResponse, String payType) {
        int couponAmount      = order.getCouponAmount();
        int pointAmount       = order.getUsePoint().intValue();

        boolean productResult = this.productService.minusQuantity(order.getOrderId());
        boolean couponResult  = this.couponService.subtractCoupon(order.getMember().getMemberId(), order.getOrderId());
        boolean pointResult   = this.pointService.givePoint(order, order.getMember(),
                order.getTotalAmount(), BigDecimal.valueOf(order.getTotalAmount().intValue() - order.getDiscountAmount().intValue()));

        if (!(productResult && couponResult && pointResult)) {
            if (approvePaymentResponse != null) {
                this.paymentCancelEvent.publishEvent(PaymentCancelEvent.builder()
                                                .payType(payType)
                                                .cancelAmount(approvePaymentResponse.getTotalAmount())
                                                .cancelTaxFreeAmount(approvePaymentResponse.getTaxFree())
                                                .transactionId(approvePaymentResponse.getTransactionId())
                                                .build());
            }

            throw new RuntimeException("결제 후처리 과정에서 오류가 발생하였습니다.");
        }

        //TODO 금액 비교 로직

        int paymentId = this.paymentRepository.insertPayment(Payment.builder()
                .paymentAt(LocalDateTime.now())
                .paymentType(payType)
                .paymentStatus(PaymentStatus.APPROVE.getValue())
                .totalAmount(order.getTotalAmount())
                .orderId(order.getOrderId())
                .build());

        if (couponAmount > 0) {
            this.paymentRepository.insertPaymentElement(PaymentElement.builder()
                    .paymentMethod(PaymentMethod.COUPON.getValue())
                    .amount(BigDecimal.valueOf(couponAmount))
                    .build());
        }

        if (pointAmount > 0) {
            this.paymentRepository.insertPaymentElement(PaymentElement.builder()
                    .paymentMethod(PaymentMethod.POINT.getValue())
                    .amount(BigDecimal.valueOf(pointAmount))
                    .build());
        }

        if(approvePaymentResponse != null) {
            String cardNumber = "N/A";
            String emptyField = null;

            this.paymentRepository.insertPaymentElement(PaymentElement.builder()
                    .paymentStatus(PaymentStatus.APPROVE.getValue())
                    .paymentId(paymentId)
                    .paymentMethod(approvePaymentResponse.getPaymentMethod()) // TODO 오는 값에 따라 분기처리
                    .transactionId(approvePaymentResponse.getTransactionId())
                    .amount(approvePaymentResponse.getTotalAmount())
                    .taxFree(approvePaymentResponse.getTaxFree())
                    .vat(approvePaymentResponse.getVat())
                    .approvedAt(approvePaymentResponse.getApprovedAt())
                    .cardType(approvePaymentResponse.getCardType())
                    .cardNumber(cardNumber)
                    .install(approvePaymentResponse.getInstall())
                    .isFreeInstall(approvePaymentResponse.getIsFreeInstall())
                    .installType(approvePaymentResponse.getInstallType())
                    .cardCorp(approvePaymentResponse.getCardCorp())
                    .cardCorpCode(approvePaymentResponse.getCardCorpCode())
                    .binNumber(approvePaymentResponse.getBinNumber())
                    .issuer(approvePaymentResponse.getIssuer())
                    .issuerCode(approvePaymentResponse.getIssuerCode())
                    .bankName(emptyField)
                    .accountNumber(emptyField)
                    .accountType(emptyField)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }

        this.orderStatusUpdateEvent.publishEvent(OrderStatusUpdateEvent.builder()
                .orderId(order.getOrderId())
                .orderStatus(OrderStatus.ORDERED.getValue())
                .build());
    }

    @Transactional
    public void callCancelProcess(CancelPayDto cancelPaymentDto, int paymentId) {
        Payment payment = this.paymentRepository.findPayment(paymentId);
        List<PaymentElement> notInPgElements = payment.getPaymentElements().stream()
                .filter(paymentElement -> !paymentElement.getTransactionId().isEmpty())
                .toList();

        this.pointService.returnPoint(payment.getOrderId());
        this.productService.returnProductQuantity(payment.getOrderId());
        this.couponService.returnCoupon(payment.getOrderId());

        int cancelId = this.paymentRepository.insertPaymentCancel(PaymentCancel.builder()
                .cancelAmount(payment.getTotalAmount())
                .cancelAt(LocalDateTime.now())
                .orderId(payment.getOrderId())
                .build());

        if (cancelPaymentDto != null) {
            this.paymentRepository.insertPaymentElement(PaymentElement.builder()
                    .paymentStatus(PaymentStatus.CANCEL.getValue())
                    .paymentId(cancelId)
                    .transactionId(cancelPaymentDto.getTransactionId())
                    .amount(BigDecimal.valueOf(cancelPaymentDto.getTotalAmount()))
                    .taxFree(BigDecimal.valueOf(cancelPaymentDto.getTaxFree()))
                    .vat(BigDecimal.valueOf(cancelPaymentDto.getVat()))
                    .approvedAt(cancelPaymentDto.getApprovedAt())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }

        for (PaymentElement paymentElement : notInPgElements) {
            paymentElement.setPaymentStatus(PaymentStatus.CANCEL.getValue());
            paymentElement.setPaymentElementId(0); //TODO 자동키 생성되는지 확인
            this.paymentRepository.insertPaymentElement(paymentElement);
        }

        this.orderStatusUpdateEvent.publishEvent(OrderStatusUpdateEvent.builder()
                .orderId(payment.getOrderId())
                .orderStatus(OrderStatus.CANCELED.getValue())
                .build());
    }
}
