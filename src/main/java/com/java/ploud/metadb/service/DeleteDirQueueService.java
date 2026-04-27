package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.entity.DeleteDirQueue;
import com.java.ploud.metadb.service.repository.DeleteDirQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteDirQueueService {
    private static final String DEL_DIR_PREFIX = "DEL_DIR:";
    private final DeleteDirQueueRepository deleteDirQueueRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void save(Long userSeq, Long dirSeq) {
        log.info("queue saved by {}, dir {}", userSeq, dirSeq);
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
