package com.java.ploud.metadb.service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "directory_share",
        indexes = {
                @Index(name = "idx_directory_share_token", columnList = "token"),
                @Index(name = "idx_directory_share_root_dir", columnList = "root_dir_seq")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_directory_share_token",
                        columnNames = "token"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SharedDir {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_seq")
    private Long shareSeq;

    /**
     * 공유가 시작되는 최상위 디렉토리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "root_dir_seq",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_directory_share_root_dir")
    )
    private Directory rootDirectory;

    /**
     * 링크 공유 접근용 랜덤 토큰
     * 조회 시작점
     */
    @Column(
            name = "token",
            nullable = false,
            length = 128,
            unique = true
    )
    private String token;

    /**
     * 공유 자체 비활성화
     */
    @Column(name = "disabled", nullable = false)
    @Builder.Default
    private boolean disabled = false;

    public void disable() {
        this.disabled = true;
    }

    public void enable() {
        this.disabled = false;
    }
}