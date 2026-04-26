package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.entity.DeleteDirQueue;
import com.java.ploud.metadb.service.queue.DelProducer;
import com.java.ploud.metadb.service.repository.DeleteDirQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteDirQueueService {
    private static final String DEL_DIR_PREFIX = "DEL_DIR:";
    private final DeleteDirQueueRepository deleteDirQueueRepository;
    private final DelProducer delProducer;

    @Transactional
    public void save(Long userSeq, Long dirSeq) {
        DeleteDirQueue delQueue = DeleteDirQueue.builder()
                .queueId(DEL_DIR_PREFIX + UUID.randomUUID())
                .userSeq(userSeq)
                .dirSeq(dirSeq)
                .executed(false)
                .build();
        deleteDirQueueRepository.save(delQueue);
        delProducer.send(delQueue);
    }
}
