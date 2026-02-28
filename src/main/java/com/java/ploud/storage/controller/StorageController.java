package com.java.ploud.storage.controller;

import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.storage.service.dto.FileUploadDto;
import com.java.ploud.storage.service.MinioService;
import com.java.ploud.storage.service.dto.PreSignedUrlDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("storages")
@RestController
public class StorageController {

    private final MinioService minioService;
    @PostMapping
    public ResponseEntity<List<PreSignedUrlDto.Response>> getPreSignedUrl(
            @AuthenticationPrincipal AuthedUserDetail userDetail, @RequestBody FileUploadDto.Request request) {
        return ResponseEntity.ok()
                .body(minioService.getPreSignedUrl(userDetail.getUserSeq(), request.getFileNames()));
    }
}
