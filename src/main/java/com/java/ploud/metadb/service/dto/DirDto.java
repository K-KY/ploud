package com.java.ploud.metadb.service.dto;

import lombok.Builder;
import lombok.Getter;

public class DirDto {

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
        private String dirName;
        private Long parentSeq;

        public Response(Long dirSeq, String dirName, Long parentSeq) {
            this.dirSeq = dirSeq;
            this.dirName = dirName;
            this.parentSeq = parentSeq;
        }
    }
}
