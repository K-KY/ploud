package com.java.ploud.metadb.service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "directory", uniqueConstraints = {
        @UniqueConstraint(
                name = "dir_owner_parent_unique",
                columnNames = {"owner_id",
                        "parent_dir_seq",
                        "dir_name"}
        )
})
public class Directory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long dirSeq;

    @Column(name = "dir_name")
    private String dirName;

    @Column(name = "owner_id")
    private String ownerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_dir_seq")
    private Directory parent;


    public Long getParentSeq() {
        if (parent == null) {
            return null;
        }
        return parent.getDirSeq();
    }
}
