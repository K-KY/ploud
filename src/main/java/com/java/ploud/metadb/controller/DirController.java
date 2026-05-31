package com.java.ploud.metadb.controller;

import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.metadb.service.DirectoryService;
import com.java.ploud.metadb.service.dto.DirDto;
import com.java.ploud.metadb.service.dto.ExploreDto;
import com.java.ploud.metadb.service.entity.Directory;
import com.java.ploud.util.PathEncryptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dirs")
public class DirController {
    private final DirectoryService directoryService;
    private final PathEncryptor pathEncryptor;

    public DirController(DirectoryService directoryService, PathEncryptor pathEncryptor) {
        this.directoryService = directoryService;
        this.pathEncryptor = pathEncryptor;
    }

    @GetMapping
    public ExploreDto getRootDir(@AuthenticationPrincipal AuthedUserDetail userDetail) {
        Directory root = directoryService.findRoot(userDetail.getUserSeq());
        return toExploreDto(userDetail.getUserSeq(), root.getDirSeq());
    }

    /**
     *
     * @return - parentSeq를 부모로 갖는 하위 디렉토리
     */
    @GetMapping("{dir}")
    public ExploreDto getDir(@AuthenticationPrincipal AuthedUserDetail userDetail,
                             @PathVariable Long dir) {
        System.out.println("DirControll+++++++++++++++++++++++++er.getDir");
        return toExploreDto(userDetail.getUserSeq(), dir);
    }

    @PostMapping("current")
    public DirDto.Response getCurrent(@AuthenticationPrincipal AuthedUserDetail userDetail,@RequestBody DirDto.Request request) {

        Directory parentDir = directoryService.findDir(userDetail.getUserSeq(), request.getDirSeq());

        return DirDto.Response.builder()
                .dirSeq(parentDir.getDirSeq())
                .dirName(parentDir.getDirName())
                .parentSeq(parentDir.getParentSeq())
                .build();
    }

    @GetMapping("path/{path}")
    public String getPath(@AuthenticationPrincipal AuthedUserDetail userDetail, @PathVariable String path) {
        return pathEncryptor.decryptString(path);
    }


    @PatchMapping
    public void deleteDir(@AuthenticationPrincipal AuthedUserDetail userDetail, @RequestBody DirDto.Request request) {
        //디렉토리 삭제
        directoryService.deleteDirSoft(userDetail.getUserSeq(), request.getDirSeq());
        //하위 디렉토리 큐 등록
        directoryService.inDeleteQueue(userDetail.getUserSeq(), request.getDirSeq());
    }
    
    @PatchMapping("move")
    public DirDto.Response moveDir(@AuthenticationPrincipal AuthedUserDetail userDetail,
                        @RequestBody DirDto.moveDirRequest request) {
        Directory directory = directoryService.changeDir(userDetail.getUserSeq(), request);
        return DirDto.Response.builder()
                .dirSeq(directory.getDirSeq())
                .dirName(directory.getDirName())
                .parentSeq(directory.getParentSeq())
                .build();
    }

    @NotNull
    private ExploreDto toExploreDto(Long userSeq, Long dirSeq) {
        List<DirDto.Response> dirs = directoryService.findChildDir(userSeq, dirSeq)
                .stream().map(d -> DirDto.Response
                        .builder()
                        .dirSeq(d.getDirSeq())
                        .dirName(d.getDirName())
                        .parentSeq(d.getParentSeq())
                        .build()).toList();
        String encryptKey = pathEncryptor.encryptKey(dirs.stream().map(DirDto.Response::getDirSeq).toList());
        String encryptPath = pathEncryptor.encryptPath(dirs.stream().map(DirDto.Response::getDirName).toList());
        return new ExploreDto(dirs, encryptKey, encryptPath);
    }
}
