package com.java.ploud.metadb.service.dto;

import lombok.Builder;
import lombok.Getter;

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
        private String ownerId;
        private String fileName;
        private Long parentSeq;

        public Response(Long dirSeq, String ownerId, String fileName, Long parentSeq) {
            this.dirSeq = dirSeq;
            this.ownerId = ownerId;
            this.fileName = fileName;
            this.parentSeq = parentSeq;
        }
    }
}
