package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.DeleteDirQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeleteDirQueueRepository extends JpaRepository<DeleteDirQueue, String> {
}
