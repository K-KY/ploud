package com.java.ploud.auth.dto;

import com.java.ploud.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String userName;
        private String userEmail;
        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Long userSeq;
        private String userEmail;
        private String userName;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Update {
        private Long userSeq;
        private String userEmail;
        private String userName;
        private String newPassword;
    }

    public static Response of(User user) {
        return Response.builder().userName(user.getUserName())
                .userEmail(user.getUserEmail()).build();
    }
}
