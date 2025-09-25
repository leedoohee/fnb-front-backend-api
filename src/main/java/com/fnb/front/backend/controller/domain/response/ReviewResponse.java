package com.fnb.front.backend.controller.domain.response;

import com.fnb.front.backend.controller.domain.ReviewAttachFile;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class ReviewResponse {
    private int id;
    private int productId;
    private String type; //매장, 상품
    private String content;
    private Date registDate;
    private int registerId;

    private List<ReviewAttachFile> attachFiles;
}
