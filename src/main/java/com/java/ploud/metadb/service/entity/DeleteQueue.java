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

@EntityListeners(AuditingEntityListener.class)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DeleteQueue {

    @Id
    private String queueId;

    private Long targetSeq;

    private Long userSeq;

    @Enumerated(EnumType.STRING)
    private TargetTypes type;
    private boolean executed;

    @CreatedDate
    private LocalDateTime executedTime;

    @LastModifiedDate
    private LocalDateTime modifiedTime;

    public void execute() {
        executed = true;
    }
}
