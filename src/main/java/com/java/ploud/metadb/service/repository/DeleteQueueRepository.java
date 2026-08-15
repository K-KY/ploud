package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.DeleteQueue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeleteQueueRepository extends JpaRepository<DeleteQueue, String> {
    Slice<DeleteQueue> findByExecutedFalse(Pageable pageRequest);
}
