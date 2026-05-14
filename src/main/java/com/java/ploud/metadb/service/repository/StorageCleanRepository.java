package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.StorageCleanQueue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageCleanRepository extends JpaRepository<StorageCleanQueue, Long> {
}
