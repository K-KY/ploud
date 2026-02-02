package com.java.ploud.storage.controller;

import com.java.ploud.storage.service.dto.FileUploadDto;
import com.java.ploud.storage.service.MinioService;
import com.java.ploud.storage.service.dto.StorageDto;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("storages")
@RestController
public class StorageController {

    private final MinioService minioService;

    //스토리지 api랑 메타데이터 api랑 분리할거
    //메타데이터에 필요한거
    /// 올리고 나면 반횐됨
    //스토리지 경로

    /// 파라미터
    //누구껀지
    //그룹

    /// 멀티파트 파일안에 있음
    //파일 사이즈
    //진짜 파일 이름
    //contentType
    //todo 서버에서 업로드 하지 않음
    @Deprecated
    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@ModelAttribute FileUploadDto.Request request) {
        try {
            MultipartFile file = request.getFile();
            String ownerId = request.getOwnerId();
            String group = request.getGroup();

            ObjectWriteResponse upload = minioService.upload(file, ownerId, group);

            return ResponseEntity.ok(new StorageDto(
                    upload.object(),
                    ownerId,
                    group,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping
    public ResponseEntity<List<String>> getPreSignedUrl(@RequestBody FileUploadDto.PreSigned request) {
        return ResponseEntity.ok()
                .body(minioService.getPreSignedUrl(request.getOwnerId(), request.getFileNames()));
    }
}
