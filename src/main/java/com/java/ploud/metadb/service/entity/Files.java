package com.java.ploud.metadb.service.entity;

import com.java.ploud.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileSeq;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

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

    private boolean deleted;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
