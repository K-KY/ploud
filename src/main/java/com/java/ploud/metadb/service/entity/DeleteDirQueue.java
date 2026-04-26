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
public class DeleteDirQueue {

    @Id
    private String queueId;

    private Long dirSeq;

    private Long userSeq;

    private boolean executed;

    @CreatedDate
    private LocalDateTime executedTime;

    @LastModifiedDate
    private LocalDateTime modifiedTime;

    public void execute() {
        executed = true;
    }
}
