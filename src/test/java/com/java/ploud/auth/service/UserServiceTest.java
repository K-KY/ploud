package com.java.ploud.auth.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void encryptTest() {
        String password = "password";

        String encode = passwordEncoder.encode("password");
        System.out.println("encode = " + encode);

        assertThat(encode).isNotEqualTo(password);
    }
}