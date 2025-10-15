package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.processor.PaymentProcessor;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.dto.CancelPaymentDto;
import com.fnb.front.backend.controller.dto.RequestCancelPaymentDto;
import com.fnb.front.backend.repository.*;
import com.fnb.front.backend.util.CommonUtil;
import com.fnb.front.backend.util.PaymentMethod;
import com.fnb.front.backend.util.PaymentType;
import com.fnb.front.backend.util.Used;
import lombok.RequiredArgsConstructor;
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

    private final ProductRepository productRepository;

    private final CouponRepository couponRepository;

    private final MemberRepository memberRepository;

    private final PointRepository pointRepository;

    @Transactional
    public boolean callPaymentProcess(String orderId, ApprovePaymentResponse approvePaymentResponse, String payType) {
        Order order = this.orderRepository.findOrder(orderId);

        boolean productResult = this.afterApproveForProduct(order.getOrderProducts());
        boolean couponResult  = this.afterApproveForCoupon(order.getMember(), order.getOrderProducts());
        boolean pointResult   = this.afterApproveForPoint(order, order.getMember(),
                order.getTotalAmount(), BigDecimal.valueOf(order.getTotalAmount().intValue() - order.getCouponAmount() - order.getUsePoint().intValue()));

        if (!(productResult && couponResult && pointResult)) {
            if (approvePaymentResponse != null) {
                PaymentProcessor paymentProcessor  = new PaymentProcessor(PayFactory.getPay(payType));
                CancelPaymentDto response          = paymentProcessor.cancel(RequestCancelPaymentDto.builder()
                        .cancelAmount(approvePaymentResponse.getTotalAmount())
                        .cancelTaxFreeAmount(approvePaymentResponse.getTaxFree())
                        .transactionId(approvePaymentResponse.getTransactionId()).build());

                //TODO 실패 로그 추가
                assert response != null : "결제취소 과정 중 오류가 발생하였습니다.";
            }

            assert false : "결제 후처리 과정에서 오류가 발생하였습니다.";
        }

        int couponAmount = order.getCouponAmount();
        int pointAmount  = order.getUsePoint().intValue();

        //TODO 금액 비교 로직

        int paymentId = this.paymentRepository.insertPayment(Payment.builder()
                .paymentAt(LocalDateTime.now())
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
                    .paymentType(PaymentType.APPROVE.getValue())
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

        return true;
    }

    @Transactional
    public boolean callCancelProcess(CancelPaymentDto cancelPaymentDto, int paymentId) {
        Payment payment = this.paymentRepository.findPayment(paymentId);

        this.afterCancelForPoint(payment.getOrder().getOrderId());
        this.afterCancelForProduct(payment.getOrder().getOrderProducts());
        this.afterCancelForCoupon(payment.getOrder().getOrderProducts());

        int cancelId = this.paymentRepository.insertPaymentCancel(PaymentCancel.builder()
                .cancelAmount(BigDecimal.valueOf(cancelPaymentDto.getTotalAmount()))
                .cancelAt(cancelPaymentDto.getCancelAt())
                .orderId(payment.getOrderId())
                .build());

        this.paymentRepository.insertPaymentElement(PaymentElement.builder()
                .paymentType(PaymentType.CANCEL.getValue())
                .paymentId(cancelId)
                .transactionId(cancelPaymentDto.getTransactionId())
                .amount(BigDecimal.valueOf(cancelPaymentDto.getTotalAmount()))
                .taxFree(BigDecimal.valueOf(cancelPaymentDto.getTaxFree()))
                .vat(BigDecimal.valueOf(cancelPaymentDto.getVat()))
                .approvedAt(cancelPaymentDto.getApprovedAt())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        return true;
    }

    private boolean afterApproveForProduct(List<OrderProduct> orderProducts) {
        for (OrderProduct orderProduct : orderProducts) {
            if(orderProduct.getProduct() != null && orderProduct.getProduct().isInfiniteQty()) {
                continue;
            }

            if (!CommonUtil.isMinAndMaxBetween(Objects.requireNonNull(orderProduct.getProduct()).getMinQuantity(),
                    orderProduct.getProduct().getMaxQuantity(), orderProduct.getQuantity())) {
                return false;
            }

            this.productRepository.updateMinusQuantity(Objects.requireNonNull(orderProduct.getProduct()).getProductId(),
                    orderProduct.getQuantity());
        }

        return true;
    }

    private boolean afterApproveForCoupon(Member member, List<OrderProduct> orderProducts) {
        List<Integer> couponIdList       = orderProducts.stream().map(OrderProduct::getCouponId).toList();
        List<MemberCoupon> memberCoupons = this.memberRepository.findMemberCoupons(member.getMemberId(), couponIdList);

        for (OrderProduct orderProduct : orderProducts) {
            int couponId = orderProduct.getCouponId();

            MemberCoupon memberCoupon = memberCoupons.stream()
                    .filter(coupon -> coupon.getCouponId() == couponId).findFirst().orElse(null);

            if (memberCoupon != null && !memberCoupon.getIsUsed().equals(Used.NOTUSED.getValue())) {
                return false;
            }

            this.couponRepository.updateUsedMemberCoupon(member.getMemberId(), couponId, "0");
        }

        return true;
    }

    private boolean afterApproveForPoint(Order order, Member member, BigDecimal totalProductAmount, BigDecimal paymentAmount) {
        //TODO 페이에 따른 추가적립

        int usePoint = order.getUsePoint().intValue();

        if (!member.isUsablePoint(usePoint)) {
            return false;
        }

        int applyPoint = this.applyGradePointForOrder(member, totalProductAmount, paymentAmount);

        MemberPoint minusPoint = MemberPoint.builder()
                .pointType(0) // 차감
                .orderId(order.getOrderId())
                .memberId(member.getMemberId())
                .amount(usePoint)
                .isUsed(Used.USED.getValue())
                .build();

        this.pointRepository.insertMemberPoint(minusPoint);

        MemberPoint plusPoint = MemberPoint.builder()
                .pointType(1) // 적립
                .orderId(order.getOrderId())
                .memberId(member.getMemberId())
                .amount(applyPoint)
                .isUsed(Used.NOTUSED.getValue())
                .build();

        this.pointRepository.insertMemberPoint(plusPoint);

        return true;
    }

    private int applyGradePointForOrder(Member member, BigDecimal totalProductAmount, BigDecimal paymentAmount) {
        MemberPointRule rule = member.getMemberGrade().getMemberPointRule();
        int point = 0;

        if(CommonUtil.isProductAmountPolicyType(rule.getApplyUnit())){
            if(CommonUtil.isMinAndMaxBetween(rule.getMinApplyAmount().intValue(), rule.getMaxApplyAmount().intValue(), totalProductAmount.intValue())) {

                PointCalculator pointCalculator = new PointCalculator(totalProductAmount,
                        rule.getAddingPointAmount(), PointFactory.getPolicy(rule.getAddingPointType()));

                point += pointCalculator.calculate().intValue();
            }
        }

        if(CommonUtil.isPaymentAmountPolicyType(rule.getApplyUnit())) {
            if(CommonUtil.isMinAndMaxBetween(rule.getMinApplyAmount().intValue(), rule.getMaxApplyAmount().intValue(), paymentAmount.intValue())) {
                PointCalculator pointCalculator = new PointCalculator(paymentAmount,
                        rule.getAddingPointAmount(), PointFactory.getPolicy(rule.getAddingPointType()));

                point += pointCalculator.calculate().intValue();
            }
        }

        return point;
    }

    private void afterCancelForPoint(String orderId) {
        MemberPoint memberPoint = this.memberRepository.findMemberPoint(orderId);
        this.memberRepository.updateMinusPoint(memberPoint.getMemberId(), memberPoint.getAmount());
        this.pointRepository.deleteMemberPoint(orderId);
    }

    private void afterCancelForCoupon(List<OrderProduct> orderProducts) {
        for (OrderProduct orderProduct : orderProducts) {

            if (orderProduct.getCoupon() == null) {
                continue;
            }

            this.couponRepository.updateUsedMemberCoupon(orderProduct.getCoupon().getMemberCoupon().getMemberId(),
                    orderProduct.getCoupon().getMemberCoupon().getCouponId(), "1");
        }
    }

    private void afterCancelForProduct(List<OrderProduct> orderProducts) {
        for (OrderProduct orderProduct : orderProducts) {
            this.productRepository.updatePlusQuantity(orderProduct.getProduct().getProductId(), orderProduct.getQuantity());
        }
    }
}
