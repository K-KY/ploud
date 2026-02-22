package com.java.ploud.jwt.dto;

import org.springframework.http.ResponseCookie;

public class JwtDto {
    public static class RefreshToken {
        private final String id;
        private final ResponseCookie token;

        public RefreshToken(String id, ResponseCookie token) {
            this.id = id;
            this.token = token;
        }

        public String getTokenString() {
            return token.toString();
        }

        public String getId() {
            return id;
        }

        public ResponseCookie getToken() {
            return token;
        }
    }
}
