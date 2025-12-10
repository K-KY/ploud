package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.entity.Directory;
import com.java.ploud.metadb.service.repository.DirectoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryService {

    private static final String ROOT = "";
    private final DirectoryRepository directoryRepository;
    private final DirectoryTransactionService directoryTransactionService;

    /**
     * 외부에서 호출하는 메서드: multipart 파일의 originalFilename을 받아
     * 디렉토리 경로를 생성하거나 조회하여 최종 Directory를 반환.
     */
    @Transactional
    public Directory findOrCreateLastParent(String originalFilename, String ownerId) {
        Directory root = findRoot(ownerId);
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Original filename cannot be null or empty");
        }

        String[] parts = originalFilename.split("/");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Original filename contains invalid characters");
        }

        Directory parent = root;
        for (int i = 0; i < parts.length - 1; i++) {
            String dirName = parts[i];
            parent = findOrCreateChildWithRetry(parent, dirName, ownerId);
        }
        return parent;
    }

    /**
     * 재시도 로직 포함 - 데드락 발생 시 재시도
     */
    private Directory findOrCreateChildWithRetry(Directory parent, String dirName, String ownerId) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                return directoryTransactionService.findOrCreateChild(
                        parent.getDirSeq(), dirName, ownerId
                );
            } catch (Exception e) {
                attempt++;

                // 데드락이나 타임아웃 에러인 경우에만 재시도
                boolean shouldRetry = isRetryable(e);
                if (!shouldRetry || attempt >= maxRetries) {
                    log.error("Failed to create directory '{}' after {} attempts", dirName, attempt);
                    throw e;
                }

                log.warn("Deadlock detected, retrying... (attempt {}/{})", attempt, maxRetries);

                //대기
                sleep(attempt);
            }
        }

        throw new RuntimeException("파일 업로드 실패");
    }

    private static void sleep(int attempt) {
        try {
            //재시도 할 때마다 대기시간 * 2 1-> 10 2 -> 20 3 -> 40
            Thread.sleep(10L * (1L << (attempt - 1)));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during retry", ie);
        }
    }

    private boolean isRetryable(Exception exception) {
        String message = exception.getMessage();
        if (message.contains("Lock wait timeout")) {
            return true;
        }
        if (message.contains("could not execute statement")) {
            return true;
        }
        return message.contains("Deadlock");
    }

    @Transactional
    public Directory createRoot(String ownerId) {
        if (directoryRepository.existsByDirNameAndParentIsNull(ownerId)) {
            throw new IllegalArgumentException("[ERROR] 이미 루트가 존재합니다.");
        }

        return directoryRepository.saveAndFlush(Directory.builder()
                .parent(null)
                .dirName(ROOT)
                .ownerId(ownerId)
                .build());
    }

    public Directory findRoot(String ownerId) {
        return directoryRepository.findByDirNameAndOwnerId(ROOT, ownerId);
    }

    public List<Directory> findChildDir(String ownerId, Long parentSeq) {
        return directoryRepository.findByOwnerIdAndParentDirSeq(ownerId, parentSeq);
    }

    public Directory findDir(String ownerId, Long dirSeq) {
        return directoryRepository.findByOwnerIdAndDirSeq(ownerId, dirSeq);
    }
}