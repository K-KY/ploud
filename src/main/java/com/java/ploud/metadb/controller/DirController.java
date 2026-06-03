package com.java.ploud.metadb.controller;

import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.metadb.service.DirectoryService;
import com.java.ploud.metadb.service.dto.DirDto;
import com.java.ploud.metadb.service.dto.ExploreDto;
import com.java.ploud.metadb.service.dto.PathDecryptDto;
import com.java.ploud.metadb.service.entity.Directory;
import com.java.ploud.util.PathEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//todo 암호화 관련 로직들 서비스 분리
@Slf4j
@RestController
@RequestMapping("/api/v1/dirs")
public class DirController {
    private static final String DIVIDER = "/";
    private final DirectoryService directoryService;
    private final PathEncryptor pathEncryptor;

    public DirController(DirectoryService directoryService, PathEncryptor pathEncryptor) {
        this.directoryService = directoryService;
        this.pathEncryptor = pathEncryptor;
    }

    @GetMapping
    public ExploreDto getRootDir(@AuthenticationPrincipal AuthedUserDetail userDetail) {
        log.info("getRootDir");
        Directory root = directoryService.findRoot(userDetail.getUserSeq());
        String eKey = pathEncryptor.encrypt(root.getDirSeq() + "");
        String ePath = pathEncryptor.encrypt(root.getDirName());
        List<DirDto.Response> dirs = directoryService.findChildDir(userDetail.getUserSeq(), root.getDirSeq())
                .stream().map(d -> DirDto.Response
                        .builder()
                        .dirSeq(d.getDirSeq())
                        .dirName(d.getDirName())
                        .parentSeq(d.getParentSeq())
                        .build()).toList();
        System.out.println("eKey = " + eKey);
        System.out.println("ePath = " + ePath);
        return new ExploreDto(dirs, eKey, ePath, root.getDirSeq());
    }

    /**
     * @return - parentSeq를 부모로 갖는 하위 디렉토리
     */
    @GetMapping("{dir}/{key}/{path}")
    public ExploreDto getDir(@AuthenticationPrincipal AuthedUserDetail userDetail,
                             @PathVariable Long dir, @PathVariable String key, @PathVariable String path) {

        log.info("getDir key = {}, path = {}", key, path);
        Directory directory = directoryService.findDir(userDetail.getUserSeq(), dir);
        List<DirDto.Response> dirs = directoryService.findChildDir(userDetail.getUserSeq(), directory.getDirSeq())
                .stream().map(d -> DirDto.Response
                        .builder()
                        .dirSeq(d.getDirSeq())
                        .dirName(d.getDirName())
                        .parentSeq(d.getParentSeq())
                        .build()).toList();
        String dKey = pathEncryptor.decryptString(key) + DIVIDER + directory.getDirSeq();
        String dPath = pathEncryptor.decryptString(path) + DIVIDER + directory.getDirName();
        String eKey = pathEncryptor.encrypt(dKey);
        String ePath = pathEncryptor.encrypt(dPath);
        return new ExploreDto(dirs, eKey, ePath, dir);
    }

    @GetMapping("up/{dir}/{key}/{path}")
    public ExploreDto getUpDir(@AuthenticationPrincipal AuthedUserDetail userDetail,
                             @PathVariable Long dir, @PathVariable String key, @PathVariable String path) {
        Directory directory = directoryService.findParent(userDetail.getUserSeq(), dir);
        List<DirDto.Response> dirs = directoryService.findChildDir(userDetail.getUserSeq(), directory.getDirSeq())
                .stream().map(d -> DirDto.Response
                        .builder()
                        .dirSeq(d.getDirSeq())
                        .dirName(d.getDirName())
                        .parentSeq(d.getParentSeq())
                        .build()).toList();
        String dKey = pathEncryptor.decryptString(key);
        String dPath = pathEncryptor.decryptString(path);
        String eKey = pathEncryptor.encrypt(dKey.substring(0, dKey.lastIndexOf(DIVIDER)));
        String ePath = pathEncryptor.encrypt(dPath.substring(0, dPath.lastIndexOf(DIVIDER)));

        return new ExploreDto(dirs, eKey, ePath, dir);
    }

    @PostMapping("current")
    public DirDto.Response getCurrent(@AuthenticationPrincipal AuthedUserDetail userDetail, @RequestBody DirDto.Request request) {

        Directory parentDir = directoryService.findDir(userDetail.getUserSeq(), request.getDirSeq());

        return DirDto.Response.builder()
                .dirSeq(parentDir.getDirSeq())
                .dirName(parentDir.getDirName())
                .parentSeq(parentDir.getParentSeq())
                .build();
    }


    @GetMapping("path/{path}/{key}")
    public PathDecryptDto getPath(@AuthenticationPrincipal AuthedUserDetail userDetail,
                                  @PathVariable String key,
                                  @PathVariable String path) {
        String dKey = pathEncryptor.decryptString(key);
        String dPath = pathEncryptor.decryptString(path);

        Directory dir = directoryService.findDir(userDetail.getUserSeq(), Long.parseLong(dKey.split("/")[0]));
        if (dir == null) {
            return null;
        }

        return new PathDecryptDto(dKey, dPath);
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
    private ExploreDto toExploreDto(Long userSeq, Long dirSeq, String key, String path) {
        Directory dir = directoryService.findDir(userSeq, dirSeq);
        List<DirDto.Response> dirs = directoryService.findChildDir(userSeq, dirSeq)
                .stream().map(d -> DirDto.Response
                        .builder()
                        .dirSeq(d.getDirSeq())
                        .dirName(d.getDirName())
                        .parentSeq(d.getParentSeq())
                        .build()).toList();
        String dKey = pathEncryptor.decryptString(key) + DIVIDER + dir.getDirSeq();
        String dPath = pathEncryptor.decryptString(path) + DIVIDER + dir.getDirName();
        String eKey = pathEncryptor.encrypt(dKey);
        String ePath = pathEncryptor.encrypt(dPath);
        String encryptKey = pathEncryptor.encryptKey(dirs.stream().map(DirDto.Response::getDirSeq).toList());
        String encryptPath = pathEncryptor.encryptPath(dirs.stream().map(DirDto.Response::getDirName).toList());
        return new ExploreDto(dirs, eKey, ePath, dir.getDirSeq());
    }
}
