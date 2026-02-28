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
    private final String originalFilename; //사용자 저장공간 경로 //user/dir/file
    private final String location; //앱 상에서 사용자가 보고있는 경로 //app/user/dir/file
    private final String contentType;
    private final Long size;
    private final Boolean isHls;
}
