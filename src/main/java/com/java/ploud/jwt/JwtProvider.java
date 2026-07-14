package com.java.ploud.jwt;


import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.jwt.dto.JwtDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
    private final Key key;
    private final long MIN = 1000L * 60; // 1분
    private final long HOUR = 1000L * 60 * 60; // 1시간
    private final long DAY = 86400L * 1000; // 1일

    @Value("${refresh.path}")
    private String refreshPath;

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성
    public String createToken(AuthedUserDetail userDetail) {
        return Jwts.builder()
                .setSubject(String.valueOf(userDetail.getUserSeq()))
                .claim("role", "ROLE_USER")
                .claim("username", userDetail.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + MIN * 5))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 유효성 검증
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public JwtDto.RefreshToken createRefreshToken() {
        String tokenId = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .setSubject(tokenId)
                .claim("type", "refresh_token")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + DAY * 30))// 30일
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new JwtDto.RefreshToken(tokenId, parseCookie(token));
    }

    private ResponseCookie parseCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .maxAge(Duration.ofDays(30))
                .httpOnly(true)//개발자 콘솔에서 읽지 못하게
                .secure(false)//https 필수 옵션인데 지금 없으니 일단 false
                .sameSite("Lax")//요청부와 응답부 도메인이 같아야하는가?
                .path(refreshPath)//이 경로로 사작하는 요청에만 이 쿠키를 자동으로 포함 시킴
                .build();
    }

    public ResponseCookie expiryCookie() {
        return ResponseCookie.from("refresh_token", "")
                .maxAge(Duration.ofDays(0))
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path(refreshPath)
                .build();
    }
}
