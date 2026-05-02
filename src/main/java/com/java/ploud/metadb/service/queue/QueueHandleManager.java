package com.java.ploud.metadb.service.queue;

import com.java.ploud.metadb.service.DeleteQueueService;
import com.java.ploud.metadb.service.dto.QueueMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//여기서 db의 백업 큐 수정
//핸들러에서 엔티티 수정
@Slf4j
public class QueueHandleManager {
    private final DeleteQueueService deleteQueueService;
    private final Map<String, QueueHandler> handlers;

    public QueueHandleManager(DeleteQueueService deleteDirQueueService, List<QueueHandler> handlers) {
        this.deleteQueueService = deleteDirQueueService;
        this.handlers = handlers.stream()
                .collect(
                        Collectors
                        .toMap(QueueHandler::getType, handler -> handler)
                );
    }

    public void handle(MapRecord<String, Object, Object> message) {
        String queueId = message.getValue().get("queueId").toString();
        String type = message.getValue().get("type").toString();
        String userSeq = message.getValue().get("user").toString();
        String target = message.getValue().get("target").toString();

        log.info("userSeq={}, targetSeq={}, type={}, queueId={}", userSeq, target, type, queueId);

        //핸들러에 메세지 레코드로 파싱해서 전달 -> 핸들러에서 message의 내부구조 몰라서 이렇게 함
        handlers.get(type).handle(new QueueMessage(queueId, type, Long.parseLong(userSeq), Long.parseLong(target)));

        deleteQueueService.execute(queueId);
    }
}
