package com.java.ploud.jwt.controller;

import com.google.common.net.HttpHeaders;
import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.auth.entity.User;
import com.java.ploud.auth.service.UserService;
import com.java.ploud.jwt.dto.JwtDto;
import com.java.ploud.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwtController {
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("refresh")
    public ResponseEntity<JwtDto.Response> refresh(@CookieValue("refresh_token") String token) {
        String tokenUser = jwtService.findAuthSeqByRefreshTokenId(token);
        User byUserSeq = userService.findByUserSeq(Long.parseLong(tokenUser));
        String accessToken = jwtService.createAccessToken(AuthedUserDetail.builder()
                .userEmail(byUserSeq.getUserEmail())
                .userSeq(byUserSeq.getUserSeq())
                .userPassword(byUserSeq.getPassword())
                .role("ROLE_USER")
                .build());
        JwtDto.RefreshToken refreshToken = jwtService.createRefreshToken(byUserSeq.getUserSeq());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshToken.getTokenString())
                .body(new JwtDto.Response(accessToken));
    }
}
