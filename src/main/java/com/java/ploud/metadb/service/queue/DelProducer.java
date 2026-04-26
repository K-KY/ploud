package com.java.ploud.metadb.service.queue;

import com.java.ploud.metadb.service.entity.DeleteDirQueue;
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

    public void send(DeleteDirQueue delQueue) {
        log.info("accepted dirSeq={}", delQueue.getDirSeq());
        Map<String, Object> data = new HashMap<>();
        data.put("dir", delQueue.getDirSeq().toString());
        data.put("user", delQueue.getUserSeq().toString());
        data.put("queueId", delQueue.getQueueId());

        redisTemplate.opsForStream().add("del-stream", data);
    }
}
