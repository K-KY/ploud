package com.java.ploud.metadb.controller;

import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.exceptions.RootNotFoundException;
import com.java.ploud.metadb.service.FileService;
import com.java.ploud.metadb.service.dto.DirectoryDto;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileDto.Response> upload(
            @AuthenticationPrincipal AuthedUserDetail userDetail,
            @RequestBody MetaDataDto request
    ) {
        try {
            return ResponseEntity.ok(fileService.uploadResponse(userDetail, request));

        } catch (RootNotFoundException e) {
            fileService.createRoot(userDetail.getUserSeq());
            return ResponseEntity.ok(fileService.uploadResponse(userDetail, request));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @GetMapping
    public ResponseEntity<List<FileDto.Response>> readFiles(@AuthenticationPrincipal AuthedUserDetail userDetail ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(toFileResponses(fileService.getRootFiles(userDetail.getUserSeq())));
    }

    @GetMapping("{dir}")
    public ResponseEntity<List<FileDto.Response>> readFiles(@AuthenticationPrincipal AuthedUserDetail userDetail, @PathVariable Long dir ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(toFileResponses(fileService.readFiles(userDetail.getUserSeq(), dir)));
    }

    @PostMapping
    public ResponseEntity<List<FileDto.Response>> readFiles(@AuthenticationPrincipal AuthedUserDetail userDetail,
                                       @RequestBody FileDto.Request request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(toFileResponses(fileService.readFiles(userDetail.getUserSeq(), request.getFileSeq())));
    }

    @PostMapping("/newroot")
    public ResponseEntity<DirectoryDto.Response> newRoot(@AuthenticationPrincipal AuthedUserDetail userDetail) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(DirectoryDto.Response.from(fileService.createRoot(userDetail.getUserSeq())));
    }

    @DeleteMapping
    public void deleteFile(@AuthenticationPrincipal AuthedUserDetail userDetail, @RequestBody FileDto.Request request) {
        fileService.deleteFileSoft(userDetail.getUserSeq(), request.getFileSeq());
    }

    @PatchMapping("name")
    public FileDto.Response renameFile(@AuthenticationPrincipal AuthedUserDetail userDetail,
                                       @RequestBody FileDto.RenameRequest request) {
        return FileDto.Response.from(fileService.renameFile(userDetail.getUserSeq(), request));
    }

    @PatchMapping
    public ResponseEntity<List<FileDto.Response>> changeDirs(@AuthenticationPrincipal AuthedUserDetail userDetail
            , @RequestBody FileChangeDto.Request request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(toFileResponses(fileService.changeDir(userDetail.getUserSeq(), request)));
    }

    private List<FileDto.Response> toFileResponses(List<Files> files) {
        return files.stream()
                .map(FileDto.Response::from)
                .toList();
    }
}
