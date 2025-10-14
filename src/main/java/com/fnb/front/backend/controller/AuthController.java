package com.fnb.front.backend.controller;

import com.fnb.front.backend.service.AuthService;
import com.fnb.front.backend.controller.domain.request.SignInRequest;
import com.fnb.front.backend.controller.domain.request.SignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/sign-in")
    public ResponseEntity<String> login(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.ok(this.authService.signIn(request));
    }

    @PostMapping("/auth/sign-up")
    public ResponseEntity<Boolean> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(this.authService.signUp(request));
    }
}
