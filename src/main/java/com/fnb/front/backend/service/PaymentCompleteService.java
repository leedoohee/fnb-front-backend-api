package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.command.AfterPaymentCancelCommand;
import com.fnb.front.backend.controller.domain.command.PaymentApproveCommand;
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
public class PaymentCompleteService {
    private final PaymentService paymentService;

    private final ProductService productService;

    private final CouponService couponService;

    private final PointService pointService;

    private final OrderService orderService;

    @Transactional
    public void handlePaymentApprove(PaymentApproveCommand command) {
        int couponAmount      = command.getOrder().getCouponAmount();
        int pointAmount       = command.getOrder().getUsePoint().intValue();

        boolean productResult = this.productService.minusQuantity(command.getOrder().getOrderProducts());
        boolean couponResult  = this.couponService.subtractCoupon(command.getOrder(), command.getOrder().getMember());
        boolean pointResult   = this.pointService.givePoint(command.getOrder(), command.getOrder().getMember());

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
                .totalAmount(command.getOrder().getTotalAmount())
                .orderId(command.getOrder().getOrderId())
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

        this.orderService.updateStatus(command.getOrder().getOrderId(), OrderStatus.ORDERED.getValue());
        //TODO 장바구니는 지우는게 맞나? DELYN 처리로 남겨두는게 맞나?
    }

    @Transactional
    public void handlePaymentCancel(AfterPaymentCancelCommand event) {
        List<PaymentElement> mustBeReturnedElements = event.getPayment().getPaymentElements().stream()
                .filter(paymentElement ->
                        paymentElement.getPaymentMethod().contains(PaymentMethod.COUPON.getValue()) ||
                                        paymentElement.getPaymentMethod().contains(PaymentMethod.POINT.getValue()))
                .toList();

        try {
            this.pointService.returnPoint(event.getOrder(), event.getOrder().getMember());
            this.productService.returnQuantity(event.getOrder().getOrderProducts());
            this.couponService.returnCoupon(event.getOrder(), event.getOrder().getMember());

            int cancelId = this.paymentService.insertPaymentCancel(PaymentCancel.builder()
                    .cancelAmount(event.getPayment().getTotalAmount())
                    .cancelAt(LocalDateTime.now())
                    .orderId(event.getPayment().getOrderId())
                    .build());

            if (event.getCancelPayDto() != null) {
                this.paymentService.insertPaymentElement(PaymentElement.builder()
                        .paymentStatus(PaymentStatus.CANCEL.getValue())
                        .paymentId(cancelId)
                        .transactionId(event.getCancelPayDto().getTransactionId())
                        .amount(BigDecimal.valueOf(event.getCancelPayDto().getTotalAmount()))
                        .taxFree(BigDecimal.valueOf(event.getCancelPayDto().getTaxFree()))
                        .vat(BigDecimal.valueOf(event.getCancelPayDto().getVat()))
                        .approvedAt(event.getCancelPayDto().getApprovedAt())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            }

            for (PaymentElement paymentElement : mustBeReturnedElements) {
                paymentElement.setPaymentStatus(PaymentStatus.CANCEL.getValue());
                paymentElement.setPaymentElementId(0); //TODO 자동키 생성되는지 확인
                this.paymentService.insertPaymentElement(paymentElement);
            }

            this.orderService.updateStatus(event.getOrder().getOrderId(), OrderStatus.CANCELED.getValue());

        } catch (Exception e) {
            //exception
            throw new RuntimeException("결제 취소 과정에서 오류가 발생하였습니다.", e);
        }
    }
}
