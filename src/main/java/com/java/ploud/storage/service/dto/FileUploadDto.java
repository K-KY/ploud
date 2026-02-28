package com.java.ploud.storage.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 스토리지에 저장할 데이터를 담은 dto
* */
public class FileUploadDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private List<PreSignedUrlDto.Request> fileNames;
    }
}
