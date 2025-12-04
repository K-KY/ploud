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
            "AND d.ownerId = :ownerId")
    Optional<Directory> findWithLock(
            @Param("dirName") String dirName,
            @Param("parentSeq") Long parentSeq,
            @Param("ownerId") String ownerId
    );


    Optional<Directory> findByDirNameAndParentAndOwnerId(String dirName, Directory parent, String ownerId);
    Optional<Directory> findByDirNameAndParentDirSeqAndOwnerId(String dirName, Long parent, String ownerId);

    boolean existsByOwnerIdAndParentAndDirName(String ownerId, Directory parent, String dirName);

    List<Directory> findByOwnerId(String ownerId);

    Optional<Directory> findByDirName(String dirName);

    Optional<Directory> findByDirNameAndParentIsNull(String dirName);

    boolean existsByDirNameAndParentIsNull(String dirName);

    Directory findByDirNameAndOwnerId(String dirName, String ownerId);

    List<Directory> findByOwnerIdAndParentDirSeq(String ownerId, Long parentSeq);
}
