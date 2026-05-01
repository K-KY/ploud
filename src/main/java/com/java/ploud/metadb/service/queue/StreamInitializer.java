package com.java.ploud.metadb.service.queue;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StreamInitializer {
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        try {
            redisTemplate.opsForStream().createGroup("del-stream", "del-group");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
