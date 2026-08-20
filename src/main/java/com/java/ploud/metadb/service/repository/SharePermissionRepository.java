package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.SharePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharePermissionRepository
        extends JpaRepository<SharePermission, Long> {

    //디렉토리 아이디로 링크 사용자 권한 확인
    Optional<SharePermission> findByShare_sharedDirSeqAndGuestIsNullAndDisabledFalse(Long sharedDirSeq);

    //디렉토리 아이디로 지정 사용자 권한 확인
    Optional<SharePermission> findByShare_sharedDirSeqAndGuest_UserSeqAndDisabledFalse(Long sharedDirSeq, Long userSeq);

    //특정 사용자의 접근 권한이 허용된 모든 디렉토리 확인
    List<SharePermission> findAllByGuest_UserSeqAndDisabledFalse(Long userSeq);

    //특정 사용자에 디렉토리 접근 권한이 있는지 확인
    boolean existsByShare_sharedDirSeqAndGuest_UserSeqAndDisabledFalse(Long sharedDirSeq, Long userSeq);

    //디렉토리의 모든 접근 제거
    void deleteAllByShare_sharedDirSeq(Long sharedDirSeq);
}