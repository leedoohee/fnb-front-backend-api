package com.fnb.front.backend.controller.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {

    @NotNull(message = "아이디는 필수값입니다.")
    private String memberId;

    @NotNull(message = "비밀번호는 필수값입니다.")
    private String password;
}
