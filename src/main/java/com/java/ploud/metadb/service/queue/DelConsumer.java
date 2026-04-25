package com.java.ploud.metadb.service.queue;

import com.java.ploud.metadb.service.DirectoryService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DelConsumer {
    private final RedisTemplate<String, Object> redisTemplate;
    private final DirectoryService directoryService;

    @PostConstruct
    public void start() {
        new Thread(this::consume).start();
    }

    public void consume() {
        while (true) {
            // 스트림 이름, 필드 key, 필드 value
            List<MapRecord<String, Object, Object>> messages =
                    redisTemplate.opsForStream().read(
                            Consumer.from("del-group", "consumer-1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(5)),
                            StreamOffset.create("del-stream", ReadOffset.lastConsumed())
                    );

            if (messages == null) continue;

            for (MapRecord<String, Object, Object> msg : messages) {
                try {
                    System.out.println("처리: " + msg.getValue());

                    //
                    process(msg);

                    //
                    redisTemplate.opsForStream()
                            .acknowledge("del-stream", "del-group", msg.getId());

                } catch (Exception e) {
                    //
                    System.out.println("처리 실패: " + msg.getId());
                }
            }
        }
    }

    private void process(MapRecord<String, Object, Object> msg) {
        System.out.println("msg = " + msg);
        System.out.println("msg.getValue().get(\"dirSeq\") = " + msg.getValue().get("dirSeq"));
        System.out.println("msg.getValue().get(\"status\") = " + msg.getValue().get("status"));
    }
}
