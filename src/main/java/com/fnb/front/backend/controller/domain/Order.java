package com.fnb.front.backend.controller.domain;

import com.fnb.front.backend.util.CommonUtil;
import com.fnb.front.backend.util.OrderStatus;
import com.fnb.front.backend.util.OrderType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@Table(name = "order_master")
public class Order {

    @Id
    @Column(name = "order_id", updatable = false, nullable = false)
    private String orderId;

    @Column(name = "member_seq", nullable = false)
    private int memberSeq;

    @Column(name = "member_id", updatable = false, nullable = false)
    private String memberId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(name = "order_date", updatable = false)
    private LocalDateTime orderDate;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "use_point", precision = 19, scale = 2)
    private BigDecimal usePoint;

    @Column(name = "order_type")
    private int orderType;

    @Column(name = "coupon_amount")
    private int couponAmount;

    @Column(name = "discount_amount", precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @OneToMany(mappedBy = "order")
    private List<OrderProduct> orderProducts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable=false, updatable=false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    public Order() {

    }

    public void build(List<OrderProduct> orderProducts, String orderId) {
        int totalCouponPrice = orderProducts.stream()
                .map(orderProduct -> orderProduct.getCouponAmount().intValue())
                .mapToInt(Integer::intValue).sum();

        int discountPrice    = orderProducts.stream()
                .map(orderProduct -> orderProduct.getDiscountAmount().intValue())
                .mapToInt(Integer::intValue).sum();

        int totalOriginPrice = orderProducts.stream()
                .map(orderProduct -> orderProduct.getPaymentAmount().intValue())
                .mapToInt(Integer::intValue).sum();

        this.orderId        = orderId;
        this.orderStatus    = OrderStatus.TEMP.getValue();
        this.orderType      = this.orderType == 0 ? OrderType.PICKUP.getValue() : OrderType.DELIVERY.getValue();
        this.discountAmount = BigDecimal.valueOf(discountPrice + this.usePoint.intValue());
        this.couponAmount   = totalCouponPrice;
        this.totalAmount    = BigDecimal.valueOf(totalOriginPrice);
        this.orderDate      = LocalDateTime.now();
        this.memberName     = this.member.getName();
        this.orderProducts  = orderProducts;
    }
}