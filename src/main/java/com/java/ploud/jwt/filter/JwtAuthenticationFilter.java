package com.java.ploud.jwt.filter;

import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null && jwtProvider.validateToken(token)) {
            Claims claims = jwtProvider.parseClaims(token);
            Long userSeq = Long.valueOf(claims.getSubject());
            String userName = claims.get("username", String.class);
            String role = claims.get("role", String.class);

            //이미 인증된 상태라 비밀번호 불필요
            AuthedUserDetail authDetails = new AuthedUserDetail(userSeq, userName, null, role);


            Authentication auth = new UsernamePasswordAuthenticationToken(
                    authDetails,
                    null,
                    List.of(new SimpleGrantedAuthority(role))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();

        return uri.equals("/actuator/health")
                || uri.equals("/actuator/prometheus");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");// 프론트가 jwt를 넣을 헤더 이름
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;//정상 동작이 아닌경우 Null
    }
}
