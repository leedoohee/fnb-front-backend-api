package com.fnb.front.backend.controller;

import com.fnb.front.backend.Service.MyPageService;
import com.fnb.front.backend.Service.ReviewService;
import com.fnb.front.backend.controller.domain.request.MyPageRequest;
import com.fnb.front.backend.controller.domain.response.MyInfoResponse;
import com.fnb.front.backend.controller.domain.response.MyOrderResponse;
import com.fnb.front.backend.controller.domain.response.PageResponse;
import com.fnb.front.backend.controller.domain.response.ReviewResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    @GetMapping("/my-page/info/{memberId}")
    public ResponseEntity<MyInfoResponse> getProductReviews(@PathVariable String memberId) {
        return ResponseEntity.ok(this.myPageService.getMyInfo(memberId));
    }

    @GetMapping("/my-page/order")
    public ResponseEntity<PageResponse<MyOrderResponse>> getMyReviews(MyPageRequest myPageRequest) {
        return ResponseEntity.ok(this.myPageService.getMyOrders(myPageRequest));
    }
}
