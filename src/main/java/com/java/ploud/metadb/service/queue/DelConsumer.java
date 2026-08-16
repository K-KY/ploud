package com.java.ploud.metadb.service.queue;

import com.java.ploud.metadb.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DelConsumer {
    private final QueueHandleManager queueHandleManager;
    private final RedisService redisService;
    private boolean recover = false;

    @Scheduled(fixedDelay = 1000)
    public void consume() throws InterruptedException {
        // 스트림 이름, 필드 key, 필드 value
        try {
            if (recover && redisService.isAlive()) {
                redisService.recoverDelQueueMessages();
                recover = false;
            }
            List<MapRecord<String, Object, Object>> messages
                    = redisService.readMessages("del-group", "consumer-1", "del-stream");
            List<MapRecord<String, Object, Object>> pending
                    = redisService.readPending("del-group", "consumer-1", "del-stream");
            if (messages.isEmpty() && pending.isEmpty()) {
                return;
            }
            log.info("scheduler running");
            handleMessage(pending);
            handleMessage(messages);

        } catch (RedisConnectionFailureException e) {
            //레디스가 중지된 경우 복구
            recover = true;
            log.warn("Redis unavailable. Recovery will run after reconnect.");
            return;
        }

    }

    private void handleMessage(List<MapRecord<String, Object, Object>> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (MapRecord<String, Object, Object> msg : messages) {

            try {
                log.info("consume msg {}", msg.getValue());

                //메세지 처리
                queueHandleManager.handle(msg);

                //처리 완료된 메세지 확인 (삭제 아님)
                redisService.acknowledge(msg.getId());

            } catch (Exception e) {
                //
                log.error("consume msg error: {} msg : {}", e, msg.getValue());
            }
        }

    }


    private void process(MapRecord<String, Object, Object> msg) {
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
