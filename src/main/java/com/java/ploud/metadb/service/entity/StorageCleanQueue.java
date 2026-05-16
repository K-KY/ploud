package com.java.ploud.metadb.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StorageCleanQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cleanSeq;
    private String storageKey;
    private Long userSeq;
    @Enumerated(EnumType.STRING)
    private TargetTypes type;
    private boolean executed;

    @CreatedDate
    private LocalDateTime created;
    @LastModifiedDate
    private LocalDateTime modified;

    public void execute() {
        executed = true;
    }
}
