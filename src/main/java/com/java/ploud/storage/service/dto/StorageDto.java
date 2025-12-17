package com.java.ploud.storage.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StorageDto {
    private final String storageKey;
    private final String ownerId;
    private final String group;
    private final String originalFilename;
    private final String contentType;
    private final Long size;
}
