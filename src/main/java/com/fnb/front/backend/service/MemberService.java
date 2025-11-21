package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.Member;
import com.fnb.front.backend.controller.domain.MemberCoupon;
import com.fnb.front.backend.controller.domain.request.SignInRequest;
import com.fnb.front.backend.controller.domain.request.SignUpRequest;
import com.fnb.front.backend.repository.MemberRepository;
import com.fnb.front.backend.security.JwtUtil;
import com.fnb.front.backend.util.MemberStatus;
import com.fnb.front.backend.util.Used;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public String signIn(SignInRequest signInRequest) {
        String memberId = signInRequest.getMemberId();
        String password = signInRequest.getPassword();

        Member member = this.memberRepository.findMember(memberId);

        if (member == null) {
            throw new NullPointerException("사용자가 존재하지 않습니다.");
        }

        if (!this.passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        this.memberRepository.updateLastLoginDate(memberId);

        return this.jwtUtil.createAccessToken(member);
    }

    public boolean signUp(SignUpRequest signUpRequest) {
        Member member = this.memberRepository.findMember(signUpRequest.getMemberId());

        if (member != null) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }

        this.memberRepository.insertMember(Member.builder()
                                    .memberId(signUpRequest.getMemberId())
                                    .name(signUpRequest.getName())
                                    .email(signUpRequest.getEmail())
                                    .password(this.passwordEncoder.encode(signUpRequest.getPassword()))
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

    public Member findMember(String memberId) {
        return this.memberRepository.findMember(memberId);
    }

    public List<MemberCoupon> findMemberCoupons(String memberId, String status) {
        return this.memberRepository.findMemberCoupons(memberId, status);
    }
}
