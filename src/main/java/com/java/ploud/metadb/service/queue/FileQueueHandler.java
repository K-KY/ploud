package com.java.ploud.metadb.service.queue;

import com.java.ploud.metadb.service.FileService;
import com.java.ploud.metadb.service.dto.QueueMessage;
import com.java.ploud.metadb.service.entity.TargetTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileQueueHandler implements QueueHandler {
    private final FileService fileService;

    public FileQueueHandler(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public String getType() {
        return TargetTypes.DEL_FILE.getType();
    }

    @Override
    public void handle(QueueMessage msg) {
        log.info("dir queue received: {}", msg.queueId());
    }
}
