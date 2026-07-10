package com.java.ploud.metadb.service.dto;

import com.java.ploud.auth.entity.User;
import com.java.ploud.metadb.service.entity.Files;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class FileDto {
    @Getter
    @Builder
    public static class Request {
        private Long dirSeq;

        public Request(Long dirSeq) {
            this.dirSeq = dirSeq;
        }

        public Request() {
        }
    }

    @Getter
    @Builder
    public static class Response {
        private Long dirSeq;
        private Long fileSeq;
        private UserSummary user;
        private String ownerId;
        private Long userSeq;
        private String userEmail;
        private String storageKey;
        private String title;
        private String fileName;
        private String fileHash;
        private String originalFilename;
        private Long size;
        private String contentType;
        private DirectoryDto.Response parent;
        private Long parentSeq;
        private boolean deleted;
        private LocalDateTime createdAt;

        public static Response from(Files file) {
            if (file == null) {
                return null;
            }

            User user = file.getUser();
            DirectoryDto.Response parent = DirectoryDto.Response.from(file.getParent());
            Long parentSeq = parent == null ? null : parent.getDirSeq();

            return Response.builder()
                    .dirSeq(file.getFileSeq())
                    .fileSeq(file.getFileSeq())
                    .user(UserSummary.from(user))
                    .ownerId(user == null ? null : user.getUserEmail())
                    .userSeq(user == null ? null : user.getUserSeq())
                    .userEmail(user == null ? null : user.getUserEmail())
                    .storageKey(file.getStorageKey())
                    .title(file.getTitle())
                    .fileName(file.getTitle())
                    .fileHash(file.getFileHash())
                    .originalFilename(file.getOriginalFilename())
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .parent(parent)
                    .parentSeq(parentSeq)
                    .deleted(file.isDeleted())
                    .createdAt(file.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class UserSummary {
        private Long userSeq;
        private String userName;
        private String userEmail;

        public static UserSummary from(User user) {
            if (user == null) {
                return null;
            }

            return UserSummary.builder()
                    .userSeq(user.getUserSeq())
                    .userName(user.getUserName())
                    .userEmail(user.getUserEmail())
                    .build();
        }
    }
}
