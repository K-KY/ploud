package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<Files, Long> {
    List<Files> findByParent_DirSeq(Long parent);
    List<Files> findByUser_UserSeqAndParent_DirSeq(Long userSeq, Long parent);
    List<Files> findByUser_UserSeqAndParent_DirSeqAndDeletedFalse(Long userSeq, Long parent);

    void deleteByUser_userSeqAndFileSeq(Long userUserSeq, Long fileSeq);

    Files findByUser_UserSeqAndFileSeq(Long userUserSeq, Long fileSeq);

    List<Files> findByUser_UserSeqAndFileHash(Long userUserSeq, String fileHash);

    //글로벌 중복 검사
    List<Files> findByFileHash(String fileHash);
}
