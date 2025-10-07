package com.fnb.front.backend.controller.domain.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyPageRequest {
    private int page;
    private int pageLimit;
    private int orderId;
    private String memberId;
    private int memberSeq;
    private String memberName;
    private List<String> orderStatus;
    private List<Integer> orderType;
    private String orderStartDate;
    private String orderEndDate;
}
