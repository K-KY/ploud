package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.entity.DeleteQueue;
import com.java.ploud.metadb.service.entity.StorageCleanQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final DeleteQueueService deleteQueueService;
    private final Duration idleTime = Duration.ofSeconds(30);


    public Boolean isAlive() {
        try {
            return redisTemplate.execute((RedisCallback<Boolean>)
                    connection -> "PONG".equals(connection.ping()));
        } catch (RedisConnectionFailureException e) {
            log.info("Redis connection failure...");

        }
        return false;
    }

    public Long acknowledge(RecordId msgId) {
        return redisTemplate.opsForStream()
                .acknowledge("del-stream", "del-group", msgId);
    }

    public List<MapRecord<String, Object, Object>> readMessages(String group, String consumer, String stream) {
        return redisTemplate.opsForStream().read(
                Consumer.from(group, consumer),
                StreamReadOptions.empty().count(500),
                StreamOffset.create(stream, ReadOffset.lastConsumed())
        );

    }

    public List<MapRecord<String, Object, Object>> readPending(String group, String consumer, String stream) {
        PendingMessages pendingMessages = redisTemplate.opsForStream()
                .pending(stream, group, Range.unbounded(), 100);

        //
        List<RecordId> recordIds = pendingMessages.stream()
                .filter(message -> message.getElapsedTimeSinceLastDelivery().compareTo(idleTime) >= 0)
                .map(PendingMessage::getId)
                .toList();

        if (recordIds.isEmpty()) {
            return List.of();
        }
        return redisTemplate.opsForStream().claim(
                        stream,
                        group,
                        consumer,
                        idleTime,
                        recordIds.toArray(RecordId[]::new)
                );
    }

    public void recoverDelQueueMessages() {
        log.info("Recovering del queue messages...");
        int page = 0;
        int size = 500;

        Slice<DeleteQueue> queues;

        do {
            queues = deleteQueueService.readNotExecuted(page++, size);
            queues.forEach(this::publish);
        } while (queues.hasNext());
    }
    public void publish(DeleteQueue delQueue) {
        Map<String, Object> data = new HashMap<>();
        data.put("target", delQueue.getTargetSeq().toString());
        data.put("type", delQueue.getType().getType());
        data.put("user", delQueue.getUserSeq().toString());
        data.put("queueId", delQueue.getQueueId());

        redisTemplate.opsForStream().add("del-stream", data);
    }

    public void publish(StorageCleanQueue storageClean) {
        Map<String, Object> data = new HashMap<>();
        data.put("target", storageClean.getCleanSeq().toString());
        data.put("type", storageClean.getType().getType());
        data.put("user", storageClean.getUserSeq().toString());
        data.put("queueId", storageClean.getCleanSeq().toString());
        redisTemplate.opsForStream().add("del-stream", data);
    }
}

