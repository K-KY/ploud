package com.java.ploud.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.ploud.auth.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TempUserService {

    private static final String TEMP_USER = "temp_user_";
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    //인증되지 않은 사용자 임시저장
    public String save(UserDto.Request user) throws JsonProcessingException {
        String token = UUID.randomUUID().toString();
        String key = TEMP_USER + token;

        String jsonString = objectMapper.writeValueAsString(user);
        stringRedisTemplate.opsForValue().set(key, jsonString, Duration.ofMinutes(10));
        return token;
    }

    //임시저장된 데이터를 영속화 하기 위해 조회
    public UserDto.Request findByToken(String token) throws JsonProcessingException {
        String key = "temp_user_" + token;
        String jsonString = stringRedisTemplate.opsForValue().get(key);
        if (jsonString == null) {
            return null;
        }
        return objectMapper.readValue(jsonString, UserDto.Request.class);
    }
}