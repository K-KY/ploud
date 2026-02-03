package com.java.ploud.storage.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PreSignedUrlDto {

    @Getter
    @AllArgsConstructor
    public static class Request {
        private String fileName;
        private String fileId;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String preSignedUrl;
        private String fileId;
        private String fileName;
    }
}
