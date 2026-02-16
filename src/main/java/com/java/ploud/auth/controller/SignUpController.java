package com.java.ploud.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.java.ploud.auth.dto.UserDto;
import com.java.ploud.auth.entity.User;
import com.java.ploud.auth.service.MailService;
import com.java.ploud.auth.service.PasswordEncryptor;
import com.java.ploud.auth.service.TempUserService;
import com.java.ploud.auth.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("signup")
@RequiredArgsConstructor
public class SignUpController {

    private final TempUserService tempUserService;
    private final UserService userService;
    private final PasswordEncryptor passwordEncryptor;
    private final MailService mailService;

    @PostMapping
    public void signup(@RequestBody UserDto.Request dto) throws JsonProcessingException, MessagingException {
        UserDto.Request encrypted = passwordEncryptor.encrypt(dto);
        String token = tempUserService.save(encrypted);
        mailService.sendMail(encrypted.getUserEmail(), "회원가입 인증", token);
    }

    @GetMapping
    public UserDto.Response verify(String token) throws JsonProcessingException {
        UserDto.Request tempUser = tempUserService.findByToken(token);
        if (tempUser == null) {
            throw new IllegalArgumentException("토큰이 존재하지 않음");
        }
        User user = userService.createUser(tempUser);//임시저장된 데이터 영속화
        return UserDto.of(user);
    }

}