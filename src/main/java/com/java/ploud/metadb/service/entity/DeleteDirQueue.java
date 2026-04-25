package com.java.ploud.metadb.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DeleteDirQueue {

    @Id
    private String queueId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Directory directory;

    private boolean executed;

    @CreatedDate
    private LocalDateTime executedTime;

    @LastModifiedDate
    private LocalDateTime modifiedTime;
}
