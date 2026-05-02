package com.java.ploud.metadb.service.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DelConsumer {
    private final RedisTemplate<String, Object> redisTemplate;
    private final QueueHandleManager queueHandleManager;

    @Scheduled(fixedDelay = 2000)
    public void consume() {
        // 스트림 이름, 필드 key, 필드 value
        List<MapRecord<String, Object, Object>> messages =
                redisTemplate.opsForStream().read(
                        Consumer.from("del-group", "consumer-1"),
                        StreamReadOptions.empty().count(500),
                        StreamOffset.create("del-stream", ReadOffset.lastConsumed())
                );

        if (messages == null || messages.isEmpty()) {
            //메세지 없을 때 처리 안된 메세지 있는지 확인
            return;
        }

        log.info("scheduler running");
        for (MapRecord<String, Object, Object> msg : messages) {
            try {
                log.info("consume msg {}", msg.getValue());

                //메세지 처리
                process(msg);

                //처리 완료된 메세지 확인 (삭제 아님)
                redisTemplate.opsForStream()
                        .acknowledge("del-stream", "del-group", msg.getId());

            } catch (Exception e) {
                //
                log.error("consume msg error: {} msg : {}", e, msg.getValue());
            }
        }
    }


    private void process(MapRecord<String, Object, Object> msg)  {
        queueHandleManager.handle(msg);
//        String userSeq = msg.getValue().get("user").toString();
//        String targetSeq = msg.getValue().get("target").toString();
//        String type = msg.getValue().get("type").toString();
//
//        //큐에 들어온 데이터 삭제 수행
//        dirQueueService.execute(msg.getValue().get("queueId").toString());
//
//        //삭제된 데이터 하위 항목 조회
//        List<Directory> childDir = directoryService.findChildDir(Long.parseLong(userSeq), Long.parseLong(targetSeq));
//        log.info("childDir = {}, size = {}", childDir, childDir.size());
//
//        childDir.forEach(child -> {
//            directoryService.inDeleteQueue(Long.parseLong(userSeq), child.getDirSeq());
//        });
    }
}
