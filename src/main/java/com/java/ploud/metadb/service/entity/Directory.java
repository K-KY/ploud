package com.java.ploud.metadb.service.entity;

import com.java.ploud.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Directory parent;

    private boolean deleted;

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

    public void changeDir(Directory target, long[] path) {
        //root는 parent가 null임
        if (this.getParent() == null) {
            throw new IllegalArgumentException("루트 디렉토리 이동 불가");
        }
        validateCycle(path);
        validatePath(target, path);
        this.parent = target;
    }

    //path에 meveDir의 key가 포함 되어있지 않은지 확인
    private void validateCycle(long[] path) {
        //path에 자신의 번호가 포함되는 요청 예외
        //마지막 번호는 자기 자신이므로 생략
        for (int i = 0; i < path.length - 1; i++) {
            if (path[i] == this.dirSeq) {
                throw new IllegalArgumentException("can not move the parent dir to child dir");//todo 커스텀 예외처리
            }
        }
    }

    //path의 마지막 경로(사용자가 현재 보고있는 화면) 와 목적지가 일치하는지 확인
    private void validatePath(Directory target, long[] path) {
        if (target.getDirSeq() != path[path.length - 1]) {
            throw new IllegalArgumentException("target Path not match");
        }
    }
}
