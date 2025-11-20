package com.java.ploud.metadb.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileSeq;

    @Column
    private String ownerSeq;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    private String title;

    @Column(name = "original_filename")
    private String originalFilename;

    private Long size;

    @Column(name = "content_type")
    private String contentType;

    @ManyToOne(fetch = FetchType.EAGER)
    private Directory parent;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
