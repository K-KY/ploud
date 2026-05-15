package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.entity.StorageCleanQueue;
import com.java.ploud.metadb.service.entity.TargetTypes;
import com.java.ploud.metadb.service.repository.StorageCleanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageCleanService {
    private final StorageCleanRepository storageCleanRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void save(Long userSeq, String storageKey) {
        StorageCleanQueue storageClean = storageCleanRepository.save(StorageCleanQueue.builder()
                .storageKey(storageKey)
                .userSeq(userSeq)
                .executed(false)
                .type(TargetTypes.DEL_STORAGE)
                .build());
        storageCleanRepository.save(storageClean);
        applicationEventPublisher.publishEvent(storageKey);
    }

    public StorageCleanQueue get(Long userSeq, Long targetSeq) {
        Optional<StorageCleanQueue> storageCleanQueue = storageCleanRepository.findByCleanSeqAndUserSeq(targetSeq, userSeq);
        if (storageCleanQueue.isPresent()) {
            return storageCleanQueue.get();
        }
        throw new IllegalArgumentException("No storage clean queue found for user seq " + userSeq);
    }
}
