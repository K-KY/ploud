package com.java.ploud.metadb.controller;

import com.java.ploud.metadb.service.FileService;
import com.java.ploud.metadb.service.entity.Files;
import com.java.ploud.storage.service.MinioService;
import io.minio.ObjectWriteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private final FileService fileService;
    private final MinioService minioService;

    public FileController(FileService fileService, MinioService minioService) {
        this.fileService = fileService;
        this.minioService = minioService;
    }


    @PostMapping
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "ownerId", required = false) String ownerId,
            @RequestParam(value = "group", required = false) String group) {

        try {
            //스토리지
            ObjectWriteResponse upload = minioService.upload(file, ownerId, group);

            //메타데이터
            Files saved = fileService.upload(file, ownerId, upload);

            return ResponseEntity.status(HttpStatus.OK).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}
