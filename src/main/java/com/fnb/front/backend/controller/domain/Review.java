package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", updatable = false, nullable = false)
    private int reviewId;

    @Column(name = "product_id", updatable = false, nullable = false)
    private int productId;

    @Column(name = "content")
    private String content;

    @Column(name = "register_date")
    private LocalDateTime registerDate;

    @Column(name = "register_id")
    private String registerId;

    @OneToMany(mappedBy = "review")
    private List<ReviewAttachFile> attachFiles;
}
