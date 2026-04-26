package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.entity.DeleteDirQueue;
import com.java.ploud.metadb.service.repository.DeleteDirQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteDirQueueService {
    private final DeleteDirQueueRepository deleteDirQueueRepository;

    @Transactional
    public void save(Long userSeq, Long dirSeq) {
        DeleteDirQueue delQueue = DeleteDirQueue.builder()
                .queueId(UUID.randomUUID().toString())
                .userSeq(userSeq)
                .dirSeq(dirSeq)
                .executed(false)
                .build();
        deleteDirQueueRepository.save(delQueue);
    }
}
