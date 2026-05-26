package com.java.ploud.metadb.controller;

import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.metadb.service.FileService;
import com.java.ploud.metadb.service.dto.FileChangeDto;
import com.java.ploud.metadb.service.dto.FileDto;
import com.java.ploud.metadb.service.dto.MetaDataDto;
import com.java.ploud.metadb.service.entity.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal AuthedUserDetail userDetail,
            @RequestBody MetaDataDto request
    ) {
        try {
            Files saved = fileService.upload(userDetail, request);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> readFiles(@AuthenticationPrincipal AuthedUserDetail userDetail ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(fileService.getRootFiles(userDetail.getUserSeq()));
    }

    @GetMapping("{dir}")
    public ResponseEntity<?> readFiles(@AuthenticationPrincipal AuthedUserDetail userDetail, @PathVariable Long dir ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(fileService.readFiles(userDetail.getUserSeq(), dir));
    }

    @PostMapping
    public ResponseEntity<?> readFiles(@AuthenticationPrincipal AuthedUserDetail userDetail,
                                       @RequestBody FileDto.Request request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(fileService.readFiles(userDetail.getUserSeq(), request.getDirSeq()));
    }

    @PostMapping("/newroot")
    public ResponseEntity<?> newRoot(@AuthenticationPrincipal AuthedUserDetail userDetail) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(fileService.createRoot(userDetail.getUserSeq()));
    }

    @DeleteMapping
    public void deleteFile(@AuthenticationPrincipal AuthedUserDetail userDetail, @RequestBody FileDto.Request request) {
        fileService.deleteFileSoft(userDetail.getUserSeq(), request.getDirSeq());
    }

    @PatchMapping
    public ResponseEntity<?> changeDirs(@AuthenticationPrincipal AuthedUserDetail userDetail, @RequestBody FileChangeDto.Request request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(fileService.changeDir(userDetail.getUserSeq(), request));
    }
}
