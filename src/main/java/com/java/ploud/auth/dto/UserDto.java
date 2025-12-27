package com.java.ploud.auth.dto;

import com.java.ploud.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UserDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Request {
        private String userName;
        private String email;
        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Long userSeq;
        private String userName;
        private String email;
    }

    public static Response of(User user) {
        return Response.builder().userName(user.getUserName())
                .email(user.getUserEmail()).build();
    }
}
