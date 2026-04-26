package com.java.ploud.metadb.service.entity;

import com.java.ploud.auth.entity.User;
import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "user_user_seq")
    private User user;

    private boolean executed;

    @CreatedDate
    private LocalDateTime executedTime;

    @LastModifiedDate
    private LocalDateTime modifiedTime;
}
