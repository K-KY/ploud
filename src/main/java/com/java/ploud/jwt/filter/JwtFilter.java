package com.java.ploud.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.ploud.auth.dto.AuthDto;
import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.jwt.dto.JwtDto;
import com.java.ploud.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

public class JwtFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;

    public JwtFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, JwtService jwtService) {
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/login");//로그인시 사용할 경로
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            AuthDto.Request authRequest
                    = objectMapper.readValue(request.getInputStream(), AuthDto.Request.class);
            UsernamePasswordAuthenticationToken token
                    = new UsernamePasswordAuthenticationToken(authRequest.getUserEmail(), authRequest.getUserPassword());
            return getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to authenticate user", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException {
        AuthedUserDetail principal = (AuthedUserDetail) authResult.getPrincipal();

        String at = jwtService.createAccessToken(principal);
        JwtDto.RefreshToken rt = jwtService.createRefreshToken(principal.getUserSeq());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.addHeader(HttpHeaders.SET_COOKIE, rt.getTokenString());

        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        Map.of("accessToken", at)
                )
        );
    }

}
