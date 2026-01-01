package com.java.ploud.auth.controller;

import com.java.ploud.auth.dto.UserDto;
import com.java.ploud.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PatchMapping
    public UserDto.Response updateUser(@RequestBody UserDto.Update dto) {
        return UserDto.of(userService.updateUser(dto));
    }
}
