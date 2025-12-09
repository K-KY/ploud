package com.java.ploud.metadb.service.dto;

import lombok.Builder;
import lombok.Getter;

public class DirDto {

    @Getter
    @Builder
    public static class Request {
        private String ownerId;
        private Long parentSeq;

        public Request(String ownerId, Long parentSeq) {
            this.ownerId = ownerId;
            this.parentSeq = parentSeq;
        }


    }

    @Getter
    @Builder
    public static class Response {
        private Long dirSeq;
        private String ownerId;
        private String dirName;
        private Long parentSeq;

        public Response(Long dirSeq, String ownerId, String dirName, Long parentSeq) {
            this.dirSeq = dirSeq;
            this.ownerId = ownerId;
            this.dirName = dirName;
            this.parentSeq = parentSeq;
        }


    }
}
