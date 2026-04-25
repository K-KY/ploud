package com.java.ploud.metadb.service.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DelProducer {
    private final RedisTemplate<String, Object> redisTemplate;

    public void send(Long dirSeq) {
        Map<String, Object> data = new HashMap<>();
        data.put("dirSeq", dirSeq.toString());

        redisTemplate.opsForStream().add("del-stream", data);
    }
}
