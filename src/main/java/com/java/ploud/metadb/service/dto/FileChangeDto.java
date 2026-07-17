package com.java.ploud.metadb.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class FileChangeDto {

    @Getter
    @Builder

    public static class Request {
        private Long targetDirSeq;
        private List<FileDto.Request> files;

        public Request() {
        }

        public Request(Long targetDirSeq, List<FileDto.Request> files) {
            this.targetDirSeq = targetDirSeq;
            this.files = files;
        }
    }
}
