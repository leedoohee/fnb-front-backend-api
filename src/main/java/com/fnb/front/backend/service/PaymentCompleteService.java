package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.command.AfterPaymentCancelCommand;
import com.fnb.front.backend.controller.domain.command.PaymentApproveCommand;
import com.fnb.front.backend.util.*;
import com.fnb.front.backend.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
                    .paymentStatus(PaymentStatus.APPROVE.getValue())
                    .taxFree(BigDecimal.ZERO)
                    .vat(BigDecimal.ZERO)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .paymentId(paymentId)
                    .build());
        }

        if (pointAmount > 0) {
            this.paymentService.insertPaymentElement(PaymentElement.builder()
                    .paymentMethod(PaymentMethod.POINT.getValue())
                    .amount(BigDecimal.valueOf(pointAmount))
                    .paymentStatus(PaymentStatus.APPROVE.getValue())
                    .taxFree(BigDecimal.ZERO)
                    .vat(BigDecimal.ZERO)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
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
        this.paymentService.updateAttemptStatus(command.getAttemptKey(), PaymentStatus.APPROVING.getValue(), PaymentStatus.APPROVE.getValue());
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

        PaymentElement paymentGateWayElement = payment.getPaymentElements().stream()
                .filter(element -> StringUtils.hasText(element.getTransactionId()))
                .findFirst().orElse(null);

        this.pointService.returnPoint(order, order.getMember());
        this.productService.returnQuantity(order.getOrderProducts());
        this.couponService.returnCoupon(order, order.getMember());

        this.paymentService.insertPaymentCancel(PaymentCancel.builder()
                .cancelAmount(payment.getTotalAmount())
                .cancelAt(LocalDateTime.now())
                .orderId(payment.getOrderId())
                .paymentId(payment.getPaymentId())
                .build());

        if (command.getCancelPayDto() != null) {
            this.paymentService.insertPaymentElement(PaymentElement.builder()
                    .paymentStatus(PaymentStatus.CANCEL.getValue())
                    .paymentMethod(Objects.requireNonNull(paymentGateWayElement).getPaymentMethod())
                    .paymentId(payment.getPaymentId())
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
            PaymentElement canceledElement =
                    PaymentElement.builder()
                            .paymentId(payment.getPaymentId())
                            .paymentStatus(PaymentStatus.CANCEL.getValue())
                            .paymentMethod(payType.getPaymentMethod())
                            .amount(payType.getAmount())
                            .taxFree(BigDecimal.ZERO)
                            .vat(BigDecimal.ZERO)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

            this.paymentService.insertPaymentElement(canceledElement);
        }

        this.orderService.updateStatus(order.getOrderId(), OrderStatus.CANCELED.getValue());
        this.paymentService.updatePaymentStatus(payment.getPaymentId(), PaymentStatus.CANCEL.getValue());
    }
}
