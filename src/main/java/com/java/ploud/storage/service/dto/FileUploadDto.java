package com.java.ploud.storage.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 스토리지에 저장할 데이터를 담은 dto
* */
public class FileUploadDto {

    @Getter
    @Builder
    public static class Request {
        private String ownerId;
        private List<PreSignedUrlDto.Request> fileNames;
    }
}
