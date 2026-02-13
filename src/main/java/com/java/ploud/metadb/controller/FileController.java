package com.java.ploud.metadb.controller;

import com.java.ploud.metadb.service.FileService;
import com.java.ploud.metadb.service.dto.FileDto;
import com.java.ploud.metadb.service.dto.MetaDataDto;
import com.java.ploud.metadb.service.entity.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Files> upload(
            @RequestBody MetaDataDto request
    ) {
        try {
            Files saved = fileService.upload(request);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> readFiles(@RequestBody FileDto.Request request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(fileService.readFiles(request.getOwnerId(), request.getParentSeq()));
    }

    @PostMapping("/newroot")
    public ResponseEntity<?> newRoot(@RequestParam(value = "ownerId") String ownerId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(fileService.createRoot(ownerId));
    }
}
