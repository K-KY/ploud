package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.Directory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Directory d WHERE d.dirName = :dirName " +
            "AND (:parentSeq IS NULL AND d.parent IS NULL OR d.parent.dirSeq = :parentSeq) " +
            "AND d.user.userSeq = :userSeq")
    Optional<Directory> findWithLock(
            @Param("dirName") String dirName,
            @Param("parentSeq") Long parentSeq,
            @Param("userSeq") Long userSeq
    );


//    Optional<Directory> findByDirNameAndParentAndOwnerId(String dirName, Directory parent, String ownerId);
//    Optional<Directory> findByDirNameAndParentDirSeqAndOwnerId(String dirName, Long parent, String ownerId);
    Optional<Directory> findByDirNameAndParentDirSeqAndUser_userSeq(String dirName, Long parent, Long userSeq);

//    boolean existsByOwnerIdAndParentAndDirName(String ownerId, Directory parent, String dirName);
//
//    List<Directory> findByOwnerId(String ownerId);

    Optional<Directory> findByDirName(String dirName);

    Optional<Directory> findByDirNameAndParentIsNull(String dirName);

    boolean existsByDirNameAndParentIsNull(String dirName);

    Boolean existsByUser_UserSeqAndParentIsNull(Long userSeq);

//    Directory findByDirNameAndOwnerId(String dirName, String ownerId);
//
//    List<Directory> findByOwnerIdAndParentDirSeq(String ownerId, Long parentSeq);

//    Directory findByOwnerIdAndDirSeq(String ownerId, Long dirSeq);

    Directory findByDirNameAndUser_UserSeq(String dirName, Long userUserSeq);

    List<Directory> findByUser_UserSeqAndParentDirSeq(Long userSeq, Long userUserSeq);

    Directory findByUser_UserSeqAndDirSeq(Long userSeq, Long dirSeq);
}
