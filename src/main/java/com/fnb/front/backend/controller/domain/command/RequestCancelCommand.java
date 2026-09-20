package com.fnb.front.backend.controller.domain.command;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RequestCancelCommand {
    private final String orderId;
    private final String memberId;

    @Builder
    public RequestCancelCommand(String orderId, String memberId) {
        this.memberId = memberId;
        this.orderId = orderId;
    }
}
