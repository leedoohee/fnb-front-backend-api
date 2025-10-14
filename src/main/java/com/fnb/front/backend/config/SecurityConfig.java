package com.fnb.front.backend.config;

import com.fnb.front.backend.security.CustomAuthenticationEntryPoint;
import com.fnb.front.backend.security.CustomUserDetailsService;
import com.fnb.front.backend.security.JwtAuthFilter;
import com.fnb.front.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig  {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    //private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    //private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        // 인증 및 토큰 발급 경로는 모두 접근 허용
                        .requestMatchers("/auth/sign-in", "/auth/sign-up").permitAll()
                        // 그 외 모든 요청은 인증(토큰) 필요
                        .anyRequest().authenticated()
                        //.requestMatchers("/**").permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http.addFilterBefore(
                new JwtAuthFilter(customUserDetailsService, jwtUtil),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}