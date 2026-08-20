package com.java.ploud.metadb.service.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 공유된 디렉토리의 하위 모든 디렉토리
 * 트리구조를 보장하지 않음
 */
@Entity
@Table(
        name = "directory_share_node",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_directory_share_node",
                        columnNames = {"share_seq", "dir_seq"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_directory_share_node_dir",
                        columnList = "dir_seq"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShareNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_node_seq")
    private Long shareNodeSeq;


    /**
     * 최상위 부모 노드
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "share_seq",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_directory_share_node_share")
    )
    private SharedDir share;

    /**
     * 해당 공유 범위에 포함되는 디렉토리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "dir_seq",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_directory_share_node_dir")
    )
    private Directory directory;

    private Boolean disabled;
}