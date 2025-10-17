package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.processor.PaymentProcessor;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.KakaoPayCancelResponse;
import com.fnb.front.backend.controller.dto.CancelPaymentDto;
import com.fnb.front.backend.controller.dto.RequestCancelPaymentDto;
import com.fnb.front.backend.repository.*;
import com.fnb.front.backend.util.*;
import com.fnb.front.backend.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AfterPaymentService {
    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final CouponService couponService;

    private final PointService pointService;

    @Transactional
    public boolean callPaymentProcess(String orderId, ApprovePaymentResponse approvePaymentResponse, String payType) {
        try {
            Order order = this.orderRepository.findOrder(orderId);

            boolean productResult = this.productService.afterApproveForProduct(order.getOrderProducts());
            boolean couponResult  = this.couponService.afterApproveForCoupon(order.getMember(), order.getOrderProducts());
            boolean pointResult   = this.pointService.afterApproveForPoint(order, order.getMember(),
                    order.getTotalAmount(), BigDecimal.valueOf(order.getTotalAmount().intValue() - order.getCouponAmount() - order.getUsePoint().intValue()));

            if (!(productResult && couponResult && pointResult)) {
                if (approvePaymentResponse != null) {
                    PaymentProcessor paymentProcessor  = new PaymentProcessor(PayFactory.getPay(payType));
                    boolean result = paymentProcessor.cancel(RequestCancelPaymentDto.builder()
                            .cancelAmount(approvePaymentResponse.getTotalAmount())
                            .cancelTaxFreeAmount(approvePaymentResponse.getTaxFree())
                            .transactionId(approvePaymentResponse.getTransactionId()).build());

                    //TODO 실패 로그 추가
                    throw new RuntimeException("결제 취소과정에서 오류가 발생하였습니다.");
                }

                return false;
            }

            int couponAmount = order.getCouponAmount();
            int pointAmount  = order.getUsePoint().intValue();

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

            this.finishOrder(orderId, OrderStatus.ORDERED.getValue());
        } catch (Exception e) {
            throw new DataAccessResourceFailureException("데이터베이스 처리과정중 오류가 발생하였습니다.");
        }

        return true;
    }

    @Transactional
    public boolean requestPaymentCancel(String orderId) {
        Payment payment = this.paymentRepository.findPayment(orderId);

        assert payment.getPaymentStatus().equals(PaymentStatus.APPROVE.getValue()): "취소할 수 없는 결제상태입니다.";

        List<PaymentElement> paymentElements = payment.getPaymentElements();
        PaymentElement paymentGateWayElement = paymentElements.stream()
                                                .filter(paymentElement -> !paymentElement.getTransactionId().isEmpty())
                                                .findFirst().orElse(null);

        if(paymentGateWayElement != null) {
            PaymentProcessor paymentProcessor  = new PaymentProcessor(PayFactory.getPay(payment.getPaymentType()));
            boolean result = paymentProcessor.cancel(RequestCancelPaymentDto.builder()
                    .cancelAmount(payment.getTotalAmount())
                    .cancelTaxFreeAmount(paymentGateWayElement.getTaxFree())
                    .transactionId(paymentGateWayElement.getTransactionId()).build());

            assert result : "결제취소 과정에서 문제가 발생하였습니다.";
        } else {
            this.callCancelProcess(null, payment.getPaymentId());
        }

        return true;
    }

    @Transactional
    public boolean callCancelProcess(CancelPaymentDto cancelPaymentDto, int paymentId) {
        try {
            Payment payment = this.paymentRepository.findPayment(paymentId);
            List<PaymentElement> exceptablePgElements = payment.getPaymentElements().stream()
                    .filter(paymentElement -> !paymentElement.getTransactionId().isEmpty())
                    .toList();

            this.pointService.afterCancelForPoint(payment.getOrder().getOrderId());
            this.productService.afterCancelForProduct(payment.getOrder().getOrderProducts());
            this.couponService.afterCancelForCoupon(payment.getOrder().getOrderProducts());

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

            for (PaymentElement paymentElement : exceptablePgElements) {
                paymentElement.setPaymentStatus(PaymentStatus.CANCEL.getValue());
                paymentElement.setPaymentElementId(0); //TODO 자동키 생성되는지 확인
                this.paymentRepository.insertPaymentElement(paymentElement);
            }

            this.finishOrder(payment.getOrder().getOrderId(), OrderStatus.CANCELED.getValue());
        } catch (Exception e) {
            throw new DataAccessResourceFailureException("데이터베이스 처리과정중 오류가 발생하였습니다.");
        }

        return true;
    }

    private void finishOrder(String orderId, String orderStatus) {
        this.orderRepository.updateOrderStatus(orderId, orderStatus);
    }
}
