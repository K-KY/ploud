package com.java.ploud.metadb.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 스토리지에 저장후 반환된 정보를 담을 dto
 * 메타 데이터 저장에 사용됨
 */
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataDto {
    private String originalFilename; //사용자 저장공간 경로 //user/dir/file
    private String location; //앱 상에서 사용자가 보고있는 경로 //app/user/dir/file
    private String contentType;
    private String storageKey;
    @NotNull
    private String fileHash;
    private Long size;
}
