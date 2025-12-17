package com.java.ploud.metadb.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 스토리지에 저장후 반환된 정보를 담을 dto
 * 메타 데이터 저장에 사용됨
 * */
@ToString
@Getter
@RequiredArgsConstructor
public class MetaDataDto {
    private final String storageKey;
    private final String ownerId;
    private final String group;
    private final String originalFilename;
    private final String contentType;
    private final Long size;
    private final Boolean isHls;
}
