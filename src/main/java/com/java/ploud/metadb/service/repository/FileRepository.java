package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<Files, Long> {
    List<Files> findByParent_DirSeq(Long parent);
    // todo : Directory에서는 OwnerId로 쓰고 있음 통일 필요
    List<Files> findByOwnerSeqAndParent_DirSeq(String ownerId, Long parent);

}
