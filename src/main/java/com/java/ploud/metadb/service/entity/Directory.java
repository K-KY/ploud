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

    public void changeDir(Directory targetDirectory) {
        validateMovable(targetDirectory);
        this.parent = targetDirectory;
    }

    private void validateMovable(Directory targetDirectory) {
        Directory node = targetDirectory;
        if (this.getParent() == null) {
            throw new IllegalArgumentException("루트 디렉토리 이동 불가");
        }

        while (node != null) {
            //목적지의 부모 디렉토리의 번호가 이동할 디렉토리 번호와 같으면 예외
            if (node.getDirSeq().equals(this.getDirSeq())) {
                throw new IllegalArgumentException("can not move the parent dir to child dir");//todo 커스텀 예외처리
            }

            Directory nodeParent = node.getParent();

            if (nodeParent != null &&
                    nodeParent.getDirSeq().equals(this.getParent().getDirSeq())) {
                break;
            }
            node = node.getParent();//한계층 위로 이동하여 검사 지속
        }
    }
}
