package com.java.ploud.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.java.ploud.auth.dto.UserDto;
import com.java.ploud.auth.entity.User;
import com.java.ploud.auth.service.PasswordEncryptor;
import com.java.ploud.auth.service.TempUserService;
import com.java.ploud.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SignUpController {

    private final TempUserService tempUserService;
    private final UserService userService;
    private final PasswordEncryptor passwordEncryptor;

    @PostMapping
    public void signUp(@RequestBody UserDto.Request dto) throws JsonProcessingException {
        UserDto.Request encrypted = passwordEncryptor.encrypt(dto);
        String token = tempUserService.save(encrypted);
        //todo 이메일 전송
    }

    @GetMapping
    public UserDto.Response verify(String token) throws JsonProcessingException {
        UserDto.Request tempUser = tempUserService.findByToken(token);
        User user = userService.createUser(tempUser);//임시저장된 데이터 영속화
        return UserDto.of(user);
    }

    @PatchMapping
    public UserDto.Response updateUser(@RequestBody UserDto.Update dto) {
        return UserDto.of(userService.updateUser(dto));
    }
}