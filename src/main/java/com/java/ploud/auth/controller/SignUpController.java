package com.java.ploud.auth.controller;

import com.java.ploud.auth.dto.UserDto;
import com.java.ploud.auth.entity.User;
import com.java.ploud.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignUpController {

    private final UserService userService;

    @PostMapping
    public UserDto.Response signUp(@RequestBody UserDto.Request dto) {
        User user = userService.createUser(dto);
        return UserDto.of(user);
    }

}