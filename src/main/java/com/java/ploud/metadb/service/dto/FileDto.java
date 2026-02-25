package com.java.ploud.metadb.service.dto;

import lombok.Builder;
import lombok.Getter;

public class FileDto {
    @Getter
    @Builder
    public static class Request {
        private String ownerId;
        private Long parentSeq;

        public Request(String ownerId, Long parentSeq) {
            this.ownerId = ownerId;
            this.parentSeq = parentSeq;
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
