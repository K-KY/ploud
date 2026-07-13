package com.java.ploud.metadb.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class SearchDto {

    @Getter
    @Builder
    public static class Response {
        private String keyword;
        private int limit;
        private List<DirectoryDto.Response> dirs;
        private List<FileDto.Response> files;

        public int getDirectoryCount() {
            return dirs == null ? 0 : dirs.size();
        }

        public int getFileCount() {
            return files == null ? 0 : files.size();
        }
    }
}
