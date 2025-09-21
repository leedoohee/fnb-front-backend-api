package com.fnb.backend.controller.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Entity
public class Product {
    @Id
    private int id;
    private String name;
    private String img;
    private String description;
    private String merchantId;
    private int price;
    private int status;
    private String type;
    private String category;
    private int isAvailableCoupon;
    private int purchaseQuantity;
    private int minPurchaseQuantity;
    private int maxPurchaseQuantity;
    private int isApplyMemberShip;
    private String applyMemberGrades;
    private String applyMemberGradeDisType;
    private BigDecimal applyMemberGradeDisAmt;

    @Transient
    private List<ProductOption> productOptions;

    public Product() {

    }

    public boolean isAvailablePurchase() {
        return this.status == 1;
    }

    public boolean isAvailableUseCoupon() {
        return this.isAvailableCoupon == 1;
    }

    public boolean isLessMinPurchaseQuantity() {
        return purchaseQuantity < this.minPurchaseQuantity;
    }

    public boolean isOverMaxPurchaseQuantity() {
        return this.maxPurchaseQuantity < this.purchaseQuantity ;
    }

    public boolean inMemberShipDiscount(Member member) {
        List<String> grades = Arrays.stream(this.applyMemberGrades.split(",")).toList();
        return this.isApplyMemberShip == 1 && grades.contains(member.getGrade());
    }

    public int getTotalPrice() {
        int optionPrice = this.productOptions.stream().map(ProductOption::getOptionPrice).mapToInt(BigDecimal::intValue).sum();
        return (this.price + optionPrice) * this.purchaseQuantity;
    }
}
