package com.java.ploud.metadb.controller;

import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.metadb.service.DirectoryService;
import com.java.ploud.metadb.service.dto.DirDto;
import com.java.ploud.metadb.service.dto.DirectoryDto;
import com.java.ploud.metadb.service.dto.DirectoryPathDto;
import com.java.ploud.metadb.service.dto.ExploreDto;
import com.java.ploud.metadb.service.entity.Directory;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/v1/dirs")
public class DirController {
    private static final String DIVIDER = "/";
    private final DirectoryService directoryService;

    public DirController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @GetMapping
    public ExploreDto getRootDir(@AuthenticationPrincipal AuthedUserDetail userDetail) {
        log.info("getRootDir");
        Directory root = directoryService.findRoot(userDetail.getUserSeq());
        List<DirectoryDto.Response> dirs = directoryService.findChildDir(userDetail.getUserSeq(), root.getDirSeq())
                .stream().map(DirectoryDto.Response::from).toList();
        return new ExploreDto(dirs, root.getDirSeq());
    }

    /**
     * @return - parentSeq를 부모로 갖는 하위 디렉토리
     */
    @GetMapping("{dir}")
    public ExploreDto getDir(@AuthenticationPrincipal AuthedUserDetail userDetail,
                             @PathVariable Long dir) {
        List<DirectoryDto.Response> dirs = directoryService.findChildDir(userDetail.getUserSeq(), dir)
                .stream().map(DirectoryDto.Response::from).toList();
        return new ExploreDto(dirs, dir);
    }

    @GetMapping("up/{dir}")
    public ExploreDto getUpDir(@AuthenticationPrincipal AuthedUserDetail userDetail,
                             @PathVariable Long dir) {
        Directory directory = directoryService.findParent(userDetail.getUserSeq(), dir);
        List<DirectoryDto.Response> dirs = directoryService.findChildDir(userDetail.getUserSeq(), directory.getDirSeq())
                .stream().map(DirectoryDto.Response::from).toList();

        return new ExploreDto(dirs, dir);
    }

    @PostMapping("current")
    public DirectoryDto.Response getCurrent(@AuthenticationPrincipal AuthedUserDetail userDetail, @RequestBody DirDto.Request request) {

        Directory parentDir = directoryService.findDir(userDetail.getUserSeq(), request.getDirSeq());

        return DirectoryDto.Response.from(parentDir);
    }

    @PatchMapping
    public void deleteDir(@AuthenticationPrincipal AuthedUserDetail userDetail, @RequestBody DirDto.Request request) {
        //디렉토리 삭제
        directoryService.deleteDirSoft(userDetail.getUserSeq(), request.getDirSeq());
        //하위 디렉토리 큐 등록
        directoryService.inDeleteQueue(userDetail.getUserSeq(), request.getDirSeq());
    }

    @PatchMapping("name")
    public DirectoryDto.Response renameDir(@AuthenticationPrincipal AuthedUserDetail userDetail,
                                           @RequestBody DirDto.RenameRequest request) {
        Directory directory = directoryService.renameDir(userDetail.getUserSeq(), request);
        return DirectoryDto.Response.from(directory);
    }
    
    @PatchMapping("move")
    public DirectoryDto.Response moveDir(@AuthenticationPrincipal AuthedUserDetail userDetail,
                                   @RequestBody DirDto.moveDirRequest request) {
        Directory directory = directoryService.changeDir(userDetail.getUserSeq(), request);
        return DirectoryDto.Response.from(directory);
    }

    @GetMapping("hierarchy/{dirSeq}")
    public List<DirectoryPathDto> getDirHierarchy(@AuthenticationPrincipal AuthedUserDetail userDetail, @PathVariable Long dirSeq) {
        List<DirectoryPathDto> dirHierarchy = directoryService.getDirHierarchy(userDetail.getUserSeq(), dirSeq);
        System.out.println("dirSeq = " + dirSeq);
        System.out.println("dirHierarchy.size() = " + dirHierarchy.size());
        return dirHierarchy;
    }



    @NotNull
    private ExploreDto toExploreDto(Long userSeq, Long dirSeq) {
        Directory dir = directoryService.findDir(userSeq, dirSeq);
        List<DirectoryDto.Response> dirs = directoryService.findChildDir(userSeq, dirSeq)
                .stream().map(DirectoryDto.Response::from).toList();
        return new ExploreDto(dirs, dir.getDirSeq());
    }
}
