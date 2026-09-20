package com.fnb.front.backend.controller;

import com.fnb.front.backend.security.CustomUserDetails;
import com.fnb.front.backend.service.MyPageService;
import com.fnb.front.backend.controller.domain.request.MyPageRequest;
import com.fnb.front.backend.controller.domain.response.MyInfoResponse;
import com.fnb.front.backend.controller.domain.response.MyOrderResponse;
import com.fnb.front.backend.controller.domain.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/my-page/info")
    public ResponseEntity<MyInfoResponse> getProductReviews(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(this.myPageService.getMyInfo(user.getUserId()));
    }

    @GetMapping("/my-page/order")
    public ResponseEntity<PageResponse<MyOrderResponse>> getMyReviews(MyPageRequest myPageRequest) {
        return ResponseEntity.ok(this.myPageService.getMyOrders(myPageRequest));
    }
}
