package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {
    Optional<Directory> findByDirNameAndParentAndOwnerId(String dirName, Directory parent, String ownerId);

    boolean existsByOwnerIdAndParentAndDirName(String ownerId, Directory parent, String dirName);

    List<Directory> findByOwnerId(String ownerId);

    Optional<Directory> findByDirName(String dirName);

    Optional<Directory> findByDirNameAndParentIsNull(String dirName);

    boolean existsByDirNameAndParentIsNull(String dirName);

    Directory findByDirNameAndOwnerId(String dirName, String ownerId);
}
