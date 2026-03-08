package com.java.ploud.metadb.service.dto;

import lombok.Builder;
import lombok.Getter;

public class DirDto {

    @Getter
    @Builder
    public static class Request {
        private Long parentSeq;

        public Request(Long parentSeq) {
            this.parentSeq = parentSeq;
        }

        public Request() {
        }
    }

    @Getter
    @Builder
    public static class Response {
        private Long dirSeq;
        private String dirName;
        private Long parentSeq;

        public Response(Long dirSeq, String dirName, Long parentSeq) {
            this.dirSeq = dirSeq;
            this.dirName = dirName;
            this.parentSeq = parentSeq;
        }
    }
}
