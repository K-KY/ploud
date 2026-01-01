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

    public User createUser(UserDto.Request dto) {
        if (userRepository.existsByUserEmail(dto.getUserEmail())) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = User.builder()
                .userEmail(dto.getUserEmail())
                .password(dto.getPassword())
                .userName(dto.getUserName()).build();
        userRepository.save(user);
        return user;
    }
}
