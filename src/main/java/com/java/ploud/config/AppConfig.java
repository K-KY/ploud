package com.java.ploud.config;

import com.java.ploud.metadb.service.DeleteQueueService;
import com.java.ploud.metadb.service.queue.QueueHandleManager;
import com.java.ploud.metadb.service.queue.QueueHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {


    /// 스프링이 알아서  QueueHandler 붙은 구현체 수집해서 주입 해줄거임
    @Bean
    public QueueHandleManager queueHandleManager(DeleteQueueService deleteDirQueueService,
                                                 List<QueueHandler> handlers) {
        return new QueueHandleManager(deleteDirQueueService, handlers);
    }
}
