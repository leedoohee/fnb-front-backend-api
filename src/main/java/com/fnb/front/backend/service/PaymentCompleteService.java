package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.command.AfterPaymentCancelCommand;
import com.fnb.front.backend.controller.domain.command.PaymentApproveCommand;
import com.fnb.front.backend.util.*;
import com.fnb.front.backend.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCompleteService {
    private final PaymentService paymentService;

    private final ProductService productService;

    private final CouponService couponService;

    private final PointService pointService;

    private final OrderService orderService;

    @Transactional
    public void handlePaymentApprove(PaymentApproveCommand command) {
        Order order           = this.orderService.findOrder(command.getOrderId());
        int couponAmount      = order.getCouponAmount();
        int pointAmount       = order.getUsePoint().intValue();

        boolean productResult = this.productService.minusQuantity(order.getOrderProducts());
        boolean couponResult  = this.couponService.subtractCoupon(order, order.getMember());
        boolean pointResult   = this.pointService.givePoint(order, order.getMember());

        if (!productResult) {
            throw new IllegalStateException("재고 차감 과정에서 오류가 발생하였습니다.");
        }

        if (!couponResult) {
            throw new IllegalStateException("쿠폰 차감 과정에서 오류가 발생하였습니다.");
        }

        if (!pointResult) {
            throw new IllegalStateException("포인트 적립 과정에서 오류가 발생하였습니다.");
        }

        //TODO 금액 비교 로직
        int paymentId = this.paymentService.insertPayment(Payment.builder()
                .paymentAt(LocalDateTime.now())
                .paymentType(command.getPayType())
                .paymentStatus(PaymentStatus.APPROVE.getValue())
                .totalAmount(order.getTotalAmount())
                .orderId(order.getOrderId())
                .build());

        if (couponAmount > 0) {
            this.paymentService.insertPaymentElement(PaymentElement.builder()
                    .paymentMethod(PaymentMethod.COUPON.getValue())
                    .amount(BigDecimal.valueOf(couponAmount))
                    .paymentId(paymentId)
                    .build());
        }

        if (pointAmount > 0) {
            this.paymentService.insertPaymentElement(PaymentElement.builder()
                    .paymentMethod(PaymentMethod.POINT.getValue())
                    .amount(BigDecimal.valueOf(pointAmount))
                    .paymentId(paymentId)
                    .build());
        }

        if(command.getResponse() != null) {
            String cardNumber = "N/A";
            String emptyField = null;

            this.paymentService.insertPaymentElement(PaymentElement.builder()
                    .paymentStatus(PaymentStatus.APPROVE.getValue())
                    .paymentId(paymentId)
                    .paymentMethod(command.getResponse().getPaymentMethod()) // TODO 오는 값에 따라 분기처리
                    .transactionId(command.getResponse().getTransactionId())
                    .amount(command.getResponse().getTotalAmount())
                    .taxFree(command.getResponse().getTaxFree())
                    .vat(command.getResponse().getVat())
                    .approvedAt(command.getResponse().getApprovedAt())
                    .cardType(command.getResponse().getCardType())
                    .cardNumber(cardNumber)
                    .install(command.getResponse().getInstall())
                    .isFreeInstall(command.getResponse().getIsFreeInstall())
                    .installType(command.getResponse().getInstallType())
                    .cardCorp(command.getResponse().getCardCorp())
                    .cardCorpCode(command.getResponse().getCardCorpCode())
                    .binNumber(command.getResponse().getBinNumber())
                    .issuer(command.getResponse().getIssuer())
                    .issuerCode(command.getResponse().getIssuerCode())
                    .bankName(emptyField)
                    .accountNumber(emptyField)
                    .accountType(emptyField)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }

        this.orderService.updateStatus(order.getOrderId(), OrderStatus.ORDERED.getValue());
        //TODO 장바구니는 지우는게 맞나? DELYN 처리로 남겨두는게 맞나?
    }

    @Transactional
    public void handlePaymentCancel(AfterPaymentCancelCommand command) {
        Order order     = this.orderService.findOrder(command.getOrderId());
        Payment payment = this.paymentService.findPayment(command.getOrderId());

        List<PaymentElement> mustBeReturnedPayTypes = payment.getPaymentElements().stream()
                .filter(paymentType ->
                        paymentType.getPaymentMethod().contains(PaymentMethod.COUPON.getValue()) ||
                                paymentType.getPaymentMethod().contains(PaymentMethod.POINT.getValue()))
                .toList();

        this.pointService.returnPoint(order, order.getMember());
        this.productService.returnQuantity(order.getOrderProducts());
        this.couponService.returnCoupon(order, order.getMember());

        int cancelId = this.paymentService.insertPaymentCancel(PaymentCancel.builder()
                .cancelAmount(payment.getTotalAmount())
                .cancelAt(LocalDateTime.now())
                .orderId(payment.getOrderId())
                .build());

        if (command.getCancelPayDto() != null) {
            this.paymentService.insertPaymentElement(PaymentElement.builder()
                    .paymentStatus(PaymentStatus.CANCEL.getValue())
                    .paymentId(cancelId)
                    .transactionId(command.getCancelPayDto().getTransactionId())
                    .amount(BigDecimal.valueOf(command.getCancelPayDto().getTotalAmount()))
                    .taxFree(BigDecimal.valueOf(command.getCancelPayDto().getTaxFree()))
                    .vat(BigDecimal.valueOf(command.getCancelPayDto().getVat()))
                    .approvedAt(command.getCancelPayDto().getApprovedAt())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }

        for (PaymentElement payType : mustBeReturnedPayTypes) {
            payType.setPaymentStatus(PaymentStatus.CANCEL.getValue());
            payType.setPaymentElementId(0); //TODO 자동키 생성되는지 확인
            this.paymentService.insertPaymentElement(payType);
        }

        this.orderService.updateStatus(order.getOrderId(), OrderStatus.CANCELED.getValue());
    }
}
