package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.DeleteQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeleteQueueRepository extends JpaRepository<DeleteQueue, String> {
}
