package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.entity.StorageCleanQueue;
import com.java.ploud.metadb.service.entity.TargetTypes;
import com.java.ploud.metadb.service.repository.StorageCleanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public StorageCleanQueue get(Long userSeq, Long targetSeq) {
        Optional<StorageCleanQueue> storageCleanQueue = storageCleanRepository.findByCleanSeqAndUserSeq(targetSeq, userSeq);
        if (storageCleanQueue.isPresent()) {
            StorageCleanQueue storageData = storageCleanQueue.get();
            storageData.execute();
            return storageData;
        }
        throw new IllegalArgumentException("No storage clean queue found for user seq " + userSeq);
    }
}
