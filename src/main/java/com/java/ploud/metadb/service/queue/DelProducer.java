package com.java.ploud.metadb.service.queue;

import com.java.ploud.metadb.service.entity.DeleteQueue;
import com.java.ploud.metadb.service.entity.StorageCleanQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DelProducer {
    private final RedisTemplate<String, String> redisTemplate;

    // 트랜잭션 이벤트 리스너
    //파라미터 타입의 이벤트가 발행되면 실행됨
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(DeleteQueue delQueue) {
        log.info("accepted dirSeq={}", delQueue.getTargetSeq());
        Map<String, Object> data = new HashMap<>();
        data.put("target", delQueue.getTargetSeq().toString());
        data.put("type", delQueue.getType().getType());
        data.put("user", delQueue.getUserSeq().toString());
        data.put("queueId", delQueue.getQueueId());

        redisTemplate.opsForStream().add("del-stream", data);
    }

    //스토리지 삭제용 프로듀서
    //StorageCleanService.save 에서 호출
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(StorageCleanQueue storageClean) {
        log.info("accepted storageKey={}", storageClean.getStorageKey());
        Map<String, Object> data = new HashMap<>();
        data.put("target", storageClean.getCleanSeq().toString());
        data.put("type", storageClean.getType().getType());
        data.put("user", storageClean.getUserSeq().toString());
        data.put("queueId", storageClean.getCleanSeq().toString());
        redisTemplate.opsForStream().add("del-stream", data);
    }
}
