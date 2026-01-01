package com.java.ploud.auth.service;

import com.java.ploud.auth.dto.UserDto;
import com.java.ploud.auth.entity.User;
import com.java.ploud.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

    //todo : 로그인 기능 완료시 변경 되어야함
    //userSeq, email도 일치 하지만 다른 사용자의 요청일 수 있음
    public User updateUser(UserDto.Update dto) {
        User user = userRepository.findById(dto.getUserSeq())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (Objects.equals(user.getUserEmail(), dto.getUserEmail())
                && Objects.equals(user.getUserName(), dto.getUserName())) {
            user.changePassword(dto.getNewPassword());
            user.changeUserName(dto.getUserName());
        }
        throw new IllegalArgumentException("Illegal update");
    }
}
