package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.entity.Directory;
import com.java.ploud.metadb.service.repository.DirectoryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 트랜잭션 분리를 위한 별도 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryTransactionService {

    private final DirectoryRepository directoryRepository;
    private final EntityManager entityManager;

    /**
     * 새로운 트랜잭션에서 디렉토리 조회 또는 생성
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Directory findOrCreateChild(Long parentSeq, String dirName, String ownerId) {
        //비관적 락으로 조회
        Optional<Directory> existing = directoryRepository.findWithLock(dirName, parentSeq, ownerId);
        if (existing.isPresent()) {
            return existing.get();
        }

        //부모 디렉토리 조회
        Directory parent = directoryRepository.findById(parentSeq)
                .orElseThrow(() -> new IllegalArgumentException("Parent directory not found: " + parentSeq));

        //없으면 생성
        return saveNewDirectory(parent, dirName, ownerId);
    }

    /**
     * 새 Directory 엔티티를 생성하여 저장하고 반환
     */
    private Directory saveNewDirectory(Directory parent, String dirName, String ownerId) {
        Long parentId = parent.getDirSeq();

        Directory d = Directory.builder()
                .dirName(dirName)
                .parent(parent)
                .ownerId(ownerId)
                .build();

        try {
            return directoryRepository.saveAndFlush(d);
        } catch (DataIntegrityViolationException e) {
            // 영속성 컨텍스트 클리어
            entityManager.clear();

            // 동시성으로 인해 다른 트랜잭션이 생성했을 수 있으므로 재조회
            Optional<Directory> again = directoryRepository
                    .findByDirNameAndParentDirSeqAndOwnerId(dirName, parentId, ownerId);

            if (again.isPresent()) {
                log.info("Directory '{}' was created by another transaction", dirName);
                return again.get();
            }

            log.error("Unexpected DataIntegrityViolationException for directory '{}'", dirName);
            throw e;
        }
    }
}