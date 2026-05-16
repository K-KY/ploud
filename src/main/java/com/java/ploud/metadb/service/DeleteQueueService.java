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
    private final DeleteQueueRepository deleteQueueRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void save(Long userSeq, Long targetSeq, TargetTypes type) {
        log.info("queue saved by user {}, target {}, type {}", userSeq, targetSeq, type);
        DeleteQueue delQueue = DeleteQueue.builder()
                .queueId(type.getPrefix() + UUID.randomUUID())
                .userSeq(userSeq)
                .targetSeq(targetSeq)
                .type(type)
                .executed(false)
                .build();
        deleteQueueRepository.save(delQueue);
        applicationEventPublisher.publishEvent(delQueue);
    }

    @Transactional
    public void execute(String queueId) {
        log.info("execute delete queue {}", queueId);
        deleteQueueRepository.findById(queueId).ifPresent(DeleteQueue::execute);
    }
}
