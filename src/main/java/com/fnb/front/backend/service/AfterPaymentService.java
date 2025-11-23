package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.event.AfterPaymentCancelEvent;
import com.fnb.front.backend.controller.domain.event.PaymentApproveEvent;
import com.fnb.front.backend.controller.domain.event.PaymentCancelEvent;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.dto.CancelPayDto;
import com.fnb.front.backend.repository.PaymentRepository;
import com.fnb.front.backend.util.*;
import com.fnb.front.backend.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AfterPaymentService {
    private final PaymentService paymentService;

    private final ProductService productService;

    private final CouponService couponService;

    private final PointService pointService;

    private final OrderService orderService;

    private final ApplicationEventPublisher paymentCancelEvent;

    @TransactionalEventListener
    public void handlePaymentApproveEvent(PaymentApproveEvent event) {
        int couponAmount      = event.getOrder().getCouponAmount();
        int pointAmount       = event.getOrder().getUsePoint().intValue();

        try {
            boolean productResult = this.productService.minusQuantity(event.getOrder().getOrderProducts());
            boolean couponResult  = this.couponService.subtractCoupon(event.getOrder(), event.getOrder().getMember());
            boolean pointResult   = this.pointService.givePoint(event.getOrder(), event.getOrder().getMember());

            assert productResult && couponResult && pointResult : "결제 후처리 과정에서 오류가 발생하였습니다.";

            //TODO 금액 비교 로직
            int paymentId = this.paymentService.insertPayment(Payment.builder()
                    .paymentAt(LocalDateTime.now())
                    .paymentType(event.getPayType())
                    .paymentStatus(PaymentStatus.APPROVE.getValue())
                    .totalAmount(event.getOrder().getTotalAmount())
                    .orderId(event.getOrder().getOrderId())
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

            if(event.getResponse() != null) {
                String cardNumber = "N/A";
                String emptyField = null;

                this.paymentService.insertPaymentElement(PaymentElement.builder()
                        .paymentStatus(PaymentStatus.APPROVE.getValue())
                        .paymentId(paymentId)
                        .paymentMethod(event.getResponse().getPaymentMethod()) // TODO 오는 값에 따라 분기처리
                        .transactionId(event.getResponse().getTransactionId())
                        .amount(event.getResponse().getTotalAmount())
                        .taxFree(event.getResponse().getTaxFree())
                        .vat(event.getResponse().getVat())
                        .approvedAt(event.getResponse().getApprovedAt())
                        .cardType(event.getResponse().getCardType())
                        .cardNumber(cardNumber)
                        .install(event.getResponse().getInstall())
                        .isFreeInstall(event.getResponse().getIsFreeInstall())
                        .installType(event.getResponse().getInstallType())
                        .cardCorp(event.getResponse().getCardCorp())
                        .cardCorpCode(event.getResponse().getCardCorpCode())
                        .binNumber(event.getResponse().getBinNumber())
                        .issuer(event.getResponse().getIssuer())
                        .issuerCode(event.getResponse().getIssuerCode())
                        .bankName(emptyField)
                        .accountNumber(emptyField)
                        .accountType(emptyField)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            }

            this.orderService.updateStatus(event.getOrder().getOrderId(), OrderStatus.ORDERED.getValue());

        } catch (Exception e) {
            //exception
            if (event.getResponse() != null) {
                this.paymentCancelEvent.publishEvent(PaymentCancelEvent.builder()
                        .transactionId(event.getResponse().getTransactionId())
                        .payType(event.getPayType())
                        .cancelAmount(event.getResponse().getTotalAmount())
                        .cancelTaxFreeAmount(event.getResponse().getTaxFree())
                        .build());

                throw new RuntimeException("결제 처리과정에서 오류가 발생하였습니다.", e);
            }
        }
    }

    @TransactionalEventListener
    public void handlePaymentCancelEvent(AfterPaymentCancelEvent event) {
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
