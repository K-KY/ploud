package com.java.ploud.auth.dto;

public class AuthDto {
    public static class Request{
        private String userEmail;
        private String userPassword;

        public Request(String userEmail, String userPassword) {
            this.userEmail = userEmail;
            this.userPassword = userPassword;
        }

        public Request() {
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getUserPassword() {
            return userPassword;
        }
    }
}
