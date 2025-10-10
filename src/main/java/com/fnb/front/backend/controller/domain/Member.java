package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor // Satisfies JPA requirement for a default constructor
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "member_id", unique = true, nullable = false)
    private String memberId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true) // Email is often unique
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    // Recommended: Use LocalDate or LocalDateTime for date/time fields
    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "status")
    private String status;

    @Column(name = "points")
    private int points;

    @Column(name = "birth_date")
    private LocalDate birthDate; // Use LocalDate fo

    @Column(name = "owned_coupon_count")
    private int ownedCouponCount;

    @Column(name = "total_order_count")
    private int totalOrderCount;

    @Column(name = "total_order_amount")
    private int totalOrderAmount;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate; // Use LocalDateTime for exact time

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "grade") // Duplicative of memberGrade, but kept per original
    private String grade;

    @OneToOne(mappedBy = "member")
    private Cart cart;

    @OneToMany(mappedBy = "member")
    private List<MemberCoupon> ownedCoupon;

    @OneToOne(mappedBy = "member")
    private MemberGrade memberGrade;

    @OneToMany(mappedBy = "member")
    private List<MemberPoint> memberPoints;

    @OneToMany(mappedBy = "member")
    private List<Order> orders;

    public boolean isCanPurchase() {
        return status.equals("1");
    }

    public boolean isUsablePoint(int usePoint) {
        return this.points >= usePoint;
    }
}
