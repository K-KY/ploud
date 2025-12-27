package com.java.ploud.auth.service;

import com.java.ploud.auth.dto.UserDto;
import com.java.ploud.auth.entity.User;
import com.java.ploud.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //todo : 이메일 인증
    public User createUser(UserDto.Request dto) {
        if (userRepository.existsByUserEmail(dto.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = User.builder()
                .userEmail(dto.getEmail())
                .password(dto.getPassword())
                .userName(dto.getUserName()).build();
        userRepository.save(user);
        return user;
    }
}
