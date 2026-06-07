package com.java.ploud.metadb.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class DirDto {

    @Getter
    @Builder
    public static class Request {
        private Long dirSeq;
        private String path;

        public Request(Long dirSeq, String path) {
            this.dirSeq = dirSeq;
            this.path = path;
        }

        public Request() {
        }
    }

    @Getter
    @Builder
    public static class moveDirRequest {
        private Long dirSeq;//이동할 디렉토리 번호
        private String dirName;//이동할 디렉토리 이름
        private Long parentSeq;//현재 디렉토리 번호
        private Long targetSeq;//이동 목적지 디렉토리 번호
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
