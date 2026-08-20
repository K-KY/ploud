package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.SharedDir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharedDirRepository extends JpaRepository<SharedDir, Long> {

    //접근 요청시 토큰으로 디렉토리 조회
    Optional<SharedDir> findByTokenAndDisabledFalse(String token);

    //사용자가 공유한 모든 디렉토리 조회
    List<SharedDir> findAllByRootDirectory_User_UserSeqAndDisabledFalse(Long ownerSeq);
}