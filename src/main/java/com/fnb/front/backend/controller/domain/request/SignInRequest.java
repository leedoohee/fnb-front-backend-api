package com.fnb.front.backend.controller.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {

    @NotBlank
    private String memberId;

    @NotBlank
    private String password;

    @Email
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String address;
}
