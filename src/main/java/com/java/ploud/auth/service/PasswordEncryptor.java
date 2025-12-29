package com.java.ploud.auth.service;

import com.java.ploud.auth.dto.UserDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncryptor {
    private final PasswordEncoder passwordEncoder;
    public PasswordEncryptor(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto.Request encrypt(UserDto.Request dto) {
        String password = passwordEncoder.encode(dto.getPassword());
        return UserDto.Request.builder()
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .password(password)
                .build();
    }
}
