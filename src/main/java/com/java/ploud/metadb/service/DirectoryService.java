package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.entity.Directory;
import com.java.ploud.metadb.service.repository.DirectoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// DirectoryService.java
@Service
@RequiredArgsConstructor
public class DirectoryService {

    private static final String ROOT = "";
    private final DirectoryRepository directoryRepository;

    /**
     * 외부에서 호출하는 메서드: multipart 파일의 originalFilename을 받아
     * 디렉토리 경로를 생성하거나 조회하여 최종 Directory를 반환.
     * /1/2/3/4/5/filename -> 5 반환
     */
    // todo: 사용자의 현재 위치 반영 -> 만약 사용자가 root/1/2/3 에 있다면 업로드 시 root/1/2/3/.../file
    public Directory findLastParent(String originalFilename, String ownerId) {
        // 동시성 문제 해결을 위햐 디렉토리 시작지점 조회하거나 새로 만들기
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
            parent = findOrCreateChild(parent, dirName, ownerId);
        }
        return parent;
    }

    /**
     * 부모 Directory와 dirName으로 DB에서 조회하고,
     * 없으면 생성하여 저장한 뒤 반환한다.
     */
    private Directory findOrCreateChild(Directory parent, String dirName, String ownerId) {
        return directoryRepository.findByDirNameAndParentAndOwnerId(dirName, parent, ownerId)
                .orElseGet(() -> saveNewDirectory(parent, dirName, ownerId));
    }

    /**
     * 새 Directory 엔티티를 생성하여 저장하고 반환.
     */

    private Directory saveNewDirectory(Directory parent, String dirName, String ownerId) {
        if (directoryRepository.existsByOwnerIdAndParentAndDirName(ownerId, parent, dirName)) {
            return directoryRepository.findByDirNameAndParentAndOwnerId(dirName, parent, ownerId)
                    .orElseThrow(() -> new IllegalArgumentException("오류 발생"));
        }
        Directory d = Directory.builder()
                .dirName(dirName)
                .parent(parent)
                .ownerId(ownerId)
                .build();
        return directoryRepository.save(d);
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
}
