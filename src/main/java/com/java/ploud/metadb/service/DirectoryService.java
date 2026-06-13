package com.java.ploud.metadb.service;

import com.java.ploud.auth.entity.User;
import com.java.ploud.metadb.service.dto.DirDto;
import com.java.ploud.metadb.service.dto.DirectoryPathDto;
import com.java.ploud.metadb.service.entity.Directory;
import com.java.ploud.metadb.service.entity.TargetTypes;
import com.java.ploud.metadb.service.repository.DirectoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryService {

    private static final String ROOT = "";
    private final DirectoryRepository directoryRepository;
    private final DirectoryTransactionService directoryTransactionService;
    private final DeleteQueueService deleteQueueService;

    /**
     * 외부에서 호출하는 메서드: multipart 파일의 originalFilename을 받아
     * 디렉토리 경로를 생성하거나 조회하여 최종 Directory를 반환.
     */
    @Transactional
    public Directory findOrCreateLastParent(String originalFilename, Long userSeq) {
        Directory root = findRoot(userSeq);
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
            parent = findOrCreateChildWithRetry(parent, dirName, userSeq);
        }
        return parent;
    }

    /**
     * 재시도 로직 포함 - 데드락 발생 시 재시도
     */
    private Directory findOrCreateChildWithRetry(Directory parent, String dirName, Long userSeq) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                return directoryTransactionService.findOrCreateChild(parent.getDirSeq(), dirName, userSeq);
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
            log.warn(exception.getMessage());
            return true;
        }
        if (message.contains("could not execute statement")) {
            log.warn(exception.getMessage());
            return true;
        }
        return message.contains("Deadlock");
    }

    @Transactional
    public Directory createRoot(Long userSeq) {
        if (directoryRepository.existsByUser_UserSeqAndParentIsNull(userSeq)) {
            throw new IllegalArgumentException("[ERROR] 이미 루트가 존재합니다.");
        }

        return directoryRepository.saveAndFlush(Directory.builder()
                .parent(null)
                .dirName(ROOT)
                .user(User.builder()
                        .userSeq(userSeq)
                        .build())
                .build());
    }

    public Directory findRoot(Long userSeq) {
        return directoryRepository.findByDirNameAndUser_UserSeq(ROOT, userSeq);
    }

    public List<Directory> findChildDir(Long userSeq, Long parentSeq) {
        log.info("findChildDir({}, {})", userSeq, parentSeq);
        if (parentSeq == null) {
            Directory root = findRoot(userSeq);
            return findChildDir(userSeq, root.getDirSeq());
        }
        return directoryRepository.findByUser_UserSeqAndParentDirSeqAndDeletedFalse(userSeq, parentSeq);
    }

    public Directory findDir(Long userSeq, Long dirSeq) {
        return directoryRepository.findByUser_UserSeqAndDirSeq(userSeq, dirSeq);
    }

    public Directory findParent(Long userSeq, Long dirSeq) {
        return findDir(userSeq, dirSeq).getParent();
    }

    @Transactional
    public void deleteDirSoft(Long userSeq, Long dirSeq) {
        //현재 디렉토리 삭제
        Directory dir = directoryRepository.findByUser_UserSeqAndDirSeq(userSeq, dirSeq);
        dir.delete();
    }

    @Transactional
    public void inDeleteQueue(Long userSeq, Long dirSeq) {
        deleteQueueService.save(userSeq, dirSeq, TargetTypes.DEL_DIR);
    }

    @Transactional
    public Directory changeDir(Long userSeq, DirDto.moveDirRequest request) {
        //현재 디렉토리를 현재 디렉토리에 이동
        if (Objects.equals(request.getDirSeq(), request.getTargetSeq())) {
            log.error("can not same current={}", request.getDirSeq());
            throw new IllegalArgumentException("can not same current dir and target dir");//todo 커스텀 예외처리
        }

        //이동할 디렉토라
        Directory moveDirectory = directoryRepository
                .findByUser_UserSeqAndDirSeqAndDeletedFalse(userSeq, request.getDirSeq());
        //목적지
        Directory targetDirectory = directoryRepository
                .findByUser_UserSeqAndDirSeqAndDeletedFalse(userSeq, request.getTargetSeq());

        moveDirectory.changeDir(targetDirectory);

        return moveDirectory;
    }

    public List<DirectoryPathDto> getDirHierarchy(Long userSeq, Long dirSeq) {
        return directoryRepository.findParentDirs(userSeq, dirSeq);
    }

}