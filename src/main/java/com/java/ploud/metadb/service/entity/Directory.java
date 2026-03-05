package com.java.ploud.metadb.service.entity;

import com.java.ploud.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@EntityListeners(AuditingEntityListener.class)
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "directory", uniqueConstraints = {
        @UniqueConstraint(
                name = "dir_owner_parent_unique",
                columnNames = {"user_seq",
                        "parent_dir_seq",
                        "dir_name"}
        )
})
public class Directory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dirSeq;

    @Column(name = "dir_name")
    private String dirName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_seq")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_dir_seq")
    private Directory parent;

    @CreatedDate
    private LocalDateTime created;


    public Long getParentSeq() {
        if (parent == null) {
            return null;
        }
        return parent.getDirSeq();
    }
}
