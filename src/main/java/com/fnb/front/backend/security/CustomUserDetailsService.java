package com.fnb.front.backend.security;

import com.fnb.front.backend.controller.domain.Member;
import com.fnb.front.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) {
        Member member = memberRepository.findMember(memberId);

        assert member != null : "사용자를 찾을 수 없습니다.";

        return new CustomUserDetails(member);
    }
}