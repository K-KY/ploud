package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.Files;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
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

    @Query("""
            SELECT f
            FROM Files f
            WHERE f.user.userSeq = :userSeq
              AND f.deleted = false
              AND (
                    LOWER(f.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(f.originalFilename) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            ORDER BY f.title ASC, f.fileSeq ASC
            """)
    List<Files> searchFiles(
            @Param("userSeq") Long userSeq,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"parent"})
    List<Files> findAllByParent_DirSeqIn(Collection<Long> dirSeqs);

}
