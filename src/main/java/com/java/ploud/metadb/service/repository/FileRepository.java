package com.java.ploud.metadb.service.repository;

import com.java.ploud.metadb.service.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<Files, Long> {
}
