package com.java.ploud.storage.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileDeleteDto {
    private String fileName;
    private String fileId;
    private String storageKey;

    public Long getFileId() {
        return Long.parseLong(fileId);
    }
}
