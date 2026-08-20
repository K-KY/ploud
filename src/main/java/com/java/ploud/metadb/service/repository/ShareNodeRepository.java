package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.ShareNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ShareNodeRepository extends JpaRepository<ShareNode, Long> {

    //공유된 디렉토리 하위에 접근 하려는 디렉토리가 있는지 확인
    boolean existsByShare_sharedDirSeqAndDirectory_DirSeqAndDisabledFalse(Long sharedDirSeq, Long dirSeq);

    //공유된 디렉토리 하위에 모든 디렉토리 노드 조회
    List<ShareNode> findAllByShare_sharedDirSeqAndDisabledFalse(Long sharedDirSeq);

    //공유된 디렉토리 하위의 모든 디렉토리의 권한 제거
    void deleteAllByShare_sharedDirSeq(Long sharedDirSeq);

    //공유된 디렉토리 하위의 특정 디렉토리의 권한 제거
    void deleteAllByShare_sharedDirSeqAndDirectory_DirSeqIn(Long sharedDirSeq, Collection<Long> dirSeqs);
}