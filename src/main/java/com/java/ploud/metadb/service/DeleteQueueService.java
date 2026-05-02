package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.entity.DeleteQueue;
import com.java.ploud.metadb.service.entity.TargetTypes;
import com.java.ploud.metadb.service.repository.DeleteQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteQueueService {
    private static final String DEL_DIR_PREFIX = "DEL_DIR:";
    private final DeleteQueueRepository deleteQueueRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void save(Long userSeq, Long dirSeq, TargetTypes type) {
        log.info("queue saved by user {}, dir {}", userSeq, dirSeq);
        DeleteQueue delQueue = DeleteQueue.builder()
                .queueId(DEL_DIR_PREFIX + UUID.randomUUID())
                .userSeq(userSeq)
                .targetSeq(dirSeq)
                .type(type)
                .executed(false)
                .build();
        deleteQueueRepository.save(delQueue);
        applicationEventPublisher.publishEvent(delQueue);
    }

    @Transactional
    public void execute(String queueId) {
        log.info("execute dir queue {}", queueId);
        deleteQueueRepository.findById(queueId).ifPresent(DeleteQueue::execute);
    }
}
