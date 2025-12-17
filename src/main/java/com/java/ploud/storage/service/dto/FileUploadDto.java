package com.java.ploud.storage.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 스토리지에 저장할 데이터를 담은 dto
* */
public class FileUploadDto {

    @Getter
    @Setter
    public static class Request {
        private MultipartFile file;
        private String ownerId;
        private String group;
        private Boolean isHls;

        public Request(MultipartFile file, String ownerId, String group, Boolean isHls) {
            this.file = file;
            this.ownerId = ownerId;
            this.group = group;
            this.isHls = isHls;
        }
    }
}
