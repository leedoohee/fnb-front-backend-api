package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.Member;
import com.fnb.front.backend.controller.domain.request.SignInRequest;
import com.fnb.front.backend.controller.domain.request.SignUpRequest;
import com.fnb.front.backend.repository.MemberRepository;
import com.fnb.front.backend.security.JwtUtil;
import com.fnb.front.backend.util.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public String signIn(SignInRequest signInRequest) {
        String memberId = signInRequest.getMemberId();
        String password = signInRequest.getPassword();

        Member member = this.memberRepository.findMember(memberId);

        assert member != null : "사용자가 존재하지 않습니다.";

        assert passwordEncoder.matches(password, member.getPassword()) : "비밀번호가 일치하지 않습니다.";

        this.memberRepository.updateLastLoginDate(memberId);

        return jwtUtil.createAccessToken(member);
    }

    public boolean signUp(SignUpRequest signUpRequest) {
        Member member = this.memberRepository.findMember(signUpRequest.getMemberId());

        assert member == null : "이미 가입된 회원아이디 입니다";

        this.memberRepository.insertMember(Member.builder()
                                    .memberId(signUpRequest.getMemberId())
                                    .name(signUpRequest.getName())
                                    .email(signUpRequest.getEmail())
                                    .password(passwordEncoder.encode(signUpRequest.getPassword()))
                                    .phoneNumber(signUpRequest.getPhone())
                                    .address(signUpRequest.getAddress())
                                    .status(MemberStatus.ACTIVE.getValue())
                                    .totalOrderCount(0)
                                    .points(0)
                                    .totalOrderAmount(0)
                                    .joinDate(LocalDate.now())
                                    .build());

        return true;
    }
}
