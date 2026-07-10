package com.java.ploud.metadb.service.dto;

import com.java.ploud.metadb.service.entity.Directory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class DirectoryDto {

    @Getter
    @Builder
    public static class Response {
        private Long dirSeq;
        private String dirName;
        private Long parentSeq;
        private Boolean isRoot;
        private boolean deleted;
        private LocalDateTime created;

        public static Response from(Directory directory) {
            if (directory == null) {
                return null;
            }

            return Response.builder()
                    .dirSeq(directory.getDirSeq())
                    .dirName(directory.getDirName())
                    .parentSeq(directory.getParentSeq())
                    .isRoot(directory.getIsRoot())
                    .deleted(directory.isDeleted())
                    .created(directory.getCreated())
                    .build();
        }
    }
}
