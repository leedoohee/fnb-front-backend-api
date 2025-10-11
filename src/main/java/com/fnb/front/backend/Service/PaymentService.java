package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.event.OrderResultEvent;
import com.fnb.front.backend.controller.domain.processor.PaymentProcessor;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.PaymentResultResponse;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.dto.ApprovePaymentDto;
import com.fnb.front.backend.controller.domain.request.Payment.RequestPayment;
import com.fnb.front.backend.repository.*;
import com.fnb.front.backend.util.CommonUtil;
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
import java.util.Objects;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final ProductRepository productRepository;

    private final CouponRepository couponRepository;

    private final MemberRepository memberRepository;

    private final PointRepository pointRepository;

    private final OrderService orderService;

    public PaymentService(PaymentRepository paymentRepository, ProductRepository productRepository, CouponRepository couponRepository, MemberRepository memberRepository, PointRepository pointRepository, OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.couponRepository = couponRepository;
        this.memberRepository = memberRepository;
        this.pointRepository = pointRepository;
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

        if(response == null) {
            return null;
        }

        Order order           = this.orderService.getOrder(response.getOrderId());
        boolean productResult = this.afterBehavingForProduct(order.getOrderProducts());
        boolean couponResult  = this.afterBehavingForCoupon(order.getMember(), order.getOrderProducts());
        boolean pointResult   = this.afterBehavingForPoint(order, order.getMember(), order.getTotalAmount(), BigDecimal.ZERO); //TODO 오더 만들때 실결제금액 넣기

        if(productResult && couponResult && pointResult) {
            this.insertPayments(response.getOrderId(), response);
        }

        return new PaymentResultResponse();
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean insertPayments(String orderId, ApprovePaymentResponse approvePaymentResponse) {
        Order order = this.orderService.getOrder(orderId);
        List<OrderProduct> orderProducts = order.getOrderProducts();

        int couponAmount = order.getOrderProducts().stream().map(OrderProduct::getCouponAmount).mapToInt(BigDecimal::intValue).sum();
        int pointAmount  = order.getUsePoint().intValue();

        //TODO 금액 비교 로직

        int paymentId = this.paymentRepository.insertPayment(Payment.builder()
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

        if(approvePaymentResponse != null) {
            String cardNumber = "N/A";
            String emptyField = null;

            this.paymentRepository.insertPaymentElement(PaymentElement.builder()
                    .paymentId(paymentId)
                    .paymentMethod(approvePaymentResponse.getPaymentMethod())
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

    private boolean afterBehavingForProduct(List<OrderProduct> orderProducts) {
        for (OrderProduct orderProduct : orderProducts) {
            if(orderProduct.getProduct() != null && orderProduct.getProduct().isInfiniteQty()) {
                continue;
            }

            if (!CommonUtil.isMinAndMaxBetween(Objects.requireNonNull(orderProduct.getProduct()).getMinQuantity(),
                    orderProduct.getProduct().getMaxQuantity(), orderProduct.getQuantity())) {
                return false;
            }

            this.productRepository.updateQuantity(Objects.requireNonNull(orderProduct.getProduct()).getId(),
                    orderProduct.getQuantity());
        }

        return true;
    }

    private boolean afterBehavingForCoupon(Member member, List<OrderProduct> orderProducts) {
        List<Integer> couponIdList       = orderProducts.stream().map(OrderProduct::getCouponId).toList();
        List<MemberCoupon> memberCoupons = this.memberRepository.findMemberCoupons(member.getMemberId(), couponIdList);

        for (OrderProduct orderProduct : orderProducts) {
            int couponId = orderProduct.getCouponId();

            MemberCoupon memberCoupon = memberCoupons.stream()
                    .filter(coupon -> coupon.getCouponId() == couponId).findFirst().orElse(null);

            if (memberCoupon != null && !memberCoupon.getIsUsed().equals("1")) {
                return false;
            }

            this.couponRepository.updateUsedMemberCoupon(member.getMemberId(), couponId);
        }

        return true;
    }

    private boolean afterBehavingForPoint(Order order, Member member, BigDecimal totalProductAmount, BigDecimal paymentAmount) {
        //TODO 페이에 따른 추가적립
        int applyPoint = this.applyPointForOrder(member, totalProductAmount, paymentAmount);

        if(!member.isUsablePoint(member.getPoints())) {
            return false;
        }

        BigDecimal usePoint = order.getUsePoint();

        MemberPoint minusPoint = MemberPoint.builder()
                .pointType(0) // 차감
                .orderId(order.getOrderId())
                .memberId(member.getId())
                .amount(usePoint.intValue())
                .isUsed("1")
                .build();

        this.pointRepository.insertMemberPoint(minusPoint);

        MemberPoint plusPoint = MemberPoint.builder()
                .pointType(1) // 적립
                .orderId(order.getOrderId())
                .memberId(member.getId())
                .amount(applyPoint)
                .isUsed("1")
                .build();

        this.pointRepository.insertMemberPoint(plusPoint);

        return true;
    }

    private int applyPointForOrder(Member member, BigDecimal totalProductAmount, BigDecimal paymentAmount) {
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
}
