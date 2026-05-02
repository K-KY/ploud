package com.java.ploud.metadb.service.queue;

import com.java.ploud.metadb.service.dto.QueueMessage;

public interface QueueHandler {
    String getType();
    void handle(QueueMessage queueMessage);
}
