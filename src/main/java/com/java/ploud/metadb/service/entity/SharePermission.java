package com.java.ploud.metadb.service.entity;

import com.java.ploud.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "share_permission",
        indexes = {
                @Index(
                        name = "idx_share_permission_share",
                        columnList = "share_seq"
                ),
                @Index(
                        name = "idx_share_permission_guest",
                        columnList = "guest_user_seq"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SharePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_seq")
    private Long permissionSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "share_seq",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_share_permission_share")
    )
    private SharedDir share;

    /**
     * null = 링크를 알고 있는 모든 사용자에게 적용되는 공개 권한
     *
     * 값 존재 = 해당 사용자에게 부여된 개별 권한
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "guest_user_seq",
            foreignKey = @ForeignKey(name = "fk_share_permission_guest")
    )
    private User guest;

    /**
     * 쓰기 권한
     */
    @Column(name = "can_create", nullable = false)
    @Builder.Default
    private boolean create = false;

    /**
     * 읽기 권한
     */
    @Column(name = "can_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    /**
     * 수정 권한
     */
    @Column(name = "can_update", nullable = false)
    @Builder.Default
    private boolean update = false;

    /**
     * 삭제 권한
     */
    @Column(name = "can_delete", nullable = false)
    @Builder.Default
    private boolean delete = false;

    @Column(name = "disabled", nullable = false)
    @Builder.Default
    private boolean disabled = false;

    public boolean isPublicPermission() {
        return guest == null;
    }

    public void disable() {
        this.disabled = true;
    }

    public void enable() {
        this.disabled = false;
    }

    public void changePermission(
            boolean create,
            boolean read,
            boolean update,
            boolean delete
    ) {
        this.create = create;
        this.read = read;
        this.update = update;
        this.delete = delete;
    }
}