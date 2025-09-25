package com.fnb.front.backend.controller.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Review {

    @Id
    private int id;
    private int productId;
    private String type; //매장, 상품
    private String content;
    private Date registDate;
    private int registerId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewId")
    private List<ReviewAttachFile> attachFiles;
}
