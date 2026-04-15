package com.java.ploud.storage.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PreSignedUrlDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String fileName;
        private Long fileId;
        private String storageKey;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String preSignedUrl;
        private String fileId;
        private String fileName;
        private String storageKey;
    }
}
