package com.java.ploud.metadb.service.queue;

import com.java.ploud.metadb.service.RedisService;
import com.java.ploud.metadb.service.entity.DeleteQueue;
import com.java.ploud.metadb.service.entity.StorageCleanQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Service
@RequiredArgsConstructor
@Slf4j
public class DelProducer {
    private final RedisService redisService;

    // 트랜잭션 이벤트 리스너
    //파라미터 타입의 이벤트가 발행되면 실행됨
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(DeleteQueue delQueue) {
        log.info("accepted dirSeq={}", delQueue.getTargetSeq());
        redisService.publish(delQueue);
    }


    //스토리지 삭제용 프로듀서
    //StorageCleanService.save 에서 호출
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(StorageCleanQueue storageClean) {
        log.info("accepted storageKey={}", storageClean.getStorageKey());
        redisService.publish(storageClean);
    }

}
