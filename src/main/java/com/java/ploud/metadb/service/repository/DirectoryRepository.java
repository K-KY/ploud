package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {

    //    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Directory d WHERE d.dirName = :dirName " +
            "AND (:parentSeq IS NULL AND d.parent IS NULL OR d.parent.dirSeq = :parentSeq) " +
            "AND d.user.userSeq = :userSeq")
    Optional<Directory> findWithLock(
            @Param("dirName") String dirName,
            @Param("parentSeq") Long parentSeq,
            @Param("userSeq") Long userSeq
    );

    Optional<Directory> findByDirNameAndParentDirSeqAndUser_userSeq(String dirName, Long parent, Long userSeq);

    Optional<Directory> findByDirName(String dirName);

    Optional<Directory> findByDirNameAndParentIsNull(String dirName);

    boolean existsByDirNameAndParentIsNull(String dirName);

    Boolean existsByUser_UserSeqAndParentIsNull(Long userSeq);

    Directory findByDirNameAndUser_UserSeq(String dirName, Long userUserSeq);

    List<Directory> findByUser_UserSeqAndParentDirSeq(Long userSeq, Long userUserSeq);

    List<Directory> findByUser_UserSeqAndParentDirSeqAndDeletedFalse(Long userUserSeq, Long parentDirSeq);

    Directory findByUser_UserSeqAndDirSeq(Long userSeq, Long dirSeq);

    Directory findByUser_UserSeqAndDirSeqAndDeletedFalse(Long userSeq, Long parentSeq);

    //트리 탐색 네이티브 쿼리
    @Query(value =
            "WITH RECURSIVE TargetAncestors AS ( " +
                    "    SELECT dir_seq, parent_dir_seq " +
                    "    FROM directory " +
                    "    WHERE dir_seq = :target " +
                    "    UNION ALL " +
                    "    SELECT d.dir_seq, d.parent_dir_seq " +
                    "    FROM directory d " +
                    "    INNER JOIN TargetAncestors ta ON d.dir_seq = ta.parent_dir_seq " +
                    ") " +
                    "SELECT COUNT(*) > 0 " +
                    "FROM TargetAncestors " +
                    "WHERE dir_seq = :move",
            nativeQuery = true)
    Long checkCircularReference(@Param("move") Long move, @Param("target") Long target);
}
