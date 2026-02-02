package com.java.ploud.storage.controller;

import com.java.ploud.storage.service.dto.FileUploadDto;
import com.java.ploud.storage.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("storages")
@RestController
public class StorageController {

    private final MinioService minioService;
    @PostMapping
    public ResponseEntity<List<String>> getPreSignedUrl(@RequestBody FileUploadDto.Request request) {
        return ResponseEntity.ok()
                .body(minioService.getPreSignedUrl(request.getOwnerId(), request.getFileNames()));
    }
}
