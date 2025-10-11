package com.fnb.front.backend.controller.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyPageRequest {

    @NotNull
    private int page;

    @NotNull
    private int pageLimit;

    @NotNull
    private int orderId;

    @NotBlank
    private String memberId;

    @NotNull
    private int memberSeq;

    @NotBlank
    private String memberName;

    @NotEmpty
    private List<String> orderStatus;

    @NotEmpty
    private List<Integer> orderType;

    @NotBlank
    private String orderStartDate;

    @NotBlank
    private String orderEndDate;
}
