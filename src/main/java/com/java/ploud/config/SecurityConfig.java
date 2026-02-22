package com.java.ploud.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.ploud.auth.service.UserService;
import com.java.ploud.jwt.JwtProvider;
import com.java.ploud.jwt.filter.JwtAuthenticationFilter;
import com.java.ploud.jwt.filter.JwtFilter;
import com.java.ploud.jwt.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;
    private final JwtService jwtService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(UserService userService, JwtService jwtService, JwtProvider jwtProvider,
                          ObjectMapper objectMapper, PasswordEncoder passwordEncoder,
                          AuthenticationConfiguration authenticationConfiguration) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager =
                authenticationConfiguration.getAuthenticationManager();

        JwtFilter jwtLoginFilter =
                new JwtFilter(authenticationManager, objectMapper, jwtService);

        http
                //CSRF 보호 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                //세션 관리 정책 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        //모든 경로 허용
                        .requestMatchers("/login")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAt(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                )


                //기본 로그인/인증 방식 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http
    ) throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        builder
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
        return builder.build();
    }
}
