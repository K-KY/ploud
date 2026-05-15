package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.StorageCleanQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageCleanRepository extends JpaRepository<StorageCleanQueue, Long> {
    Optional<StorageCleanQueue> findByCleanSeqAndUserSeq(Long cleanSeq, Long userSeq);
}
