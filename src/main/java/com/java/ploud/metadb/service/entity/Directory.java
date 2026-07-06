package com.java.ploud.metadb.service.entity;

import com.java.ploud.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

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
        ),
        @UniqueConstraint(
                name = "dir_owner_only_one_root",
                columnNames = {
                        "user_seq",
                        "is_root"
                }
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Directory parent;

    private boolean deleted;

    private Boolean isRoot;

    @CreatedDate
    private LocalDateTime created;

    public Long getParentSeq() {
        if (parent == null) {
            return null;
        }
        return parent.getDirSeq();
    }

    public void delete() {
        deleted = true;
    }

    public void changeDir(Directory target) {
        //root는 parent가 null임
        if (this.getParent() == null) {
            throw new IllegalArgumentException("루트 디렉토리 이동 불가");
        }
        if (validate(target)) {
            this.parent = target;
        }
    }

    public boolean validate(Directory target) {

        if (Objects.equals(target.dirSeq, this.dirSeq)) {
            return false;
        }
        Directory targetParent = target.parent;
        while (targetParent != null) {
            //만약 타겟의 디렉토리 번호가 이동하려는 디렉토리의 번호와 같다면 false
            if (Objects.equals(targetParent.dirSeq, this.dirSeq)) {
                return false;
            }
            targetParent = targetParent.parent;
        }
        return true;
    }

}
