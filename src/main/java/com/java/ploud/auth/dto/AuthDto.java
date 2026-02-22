package com.java.ploud.auth.dto;

public class AuthDto {
    public static class Request{
        private String userEmail;
        private String userPassword;

        public String getUserEmail() {
            return userEmail;
        }

        public String getUserPassword() {
            return userPassword;
        }
    }
}
