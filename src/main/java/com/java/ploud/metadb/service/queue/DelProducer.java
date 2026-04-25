package com.java.ploud.metadb.service.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DelProducer {
    private final RedisTemplate<String, Object> redisTemplate;

    public void send(Long dirSeq) {
        log.info("accepted dirSeq={}", dirSeq);
        Map<String, Object> data = new HashMap<>();
        data.put("dirSeq", dirSeq.toString());

        redisTemplate.opsForStream().add("del-stream", data);
    }
}
