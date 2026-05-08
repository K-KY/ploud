package com.java.ploud.metadb.controller;

import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.metadb.service.DirectoryService;
import com.java.ploud.metadb.service.dto.DirDto;
import com.java.ploud.metadb.service.entity.Directory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dirs")
public class DirController {
    private final DirectoryService directoryService;

    public DirController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    /**
     *
     * @param request - 조회용 postAPI
     * @return - parentSeq를 부모로 갖는 하위 디렉토리
     */
    @PostMapping
    public List<DirDto.Response> getDir(@AuthenticationPrincipal AuthedUserDetail userDetail, @RequestBody DirDto.Request request) {
        return directoryService.findChildDir(userDetail.getUserSeq(), request.getDirSeq())
                .stream().map(d -> DirDto.Response
                        .builder()
                        .dirSeq(d.getDirSeq())
                        .dirName(d.getDirName())
                        .parentSeq(d.getParentSeq())
                        .build()).toList();
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

    @PatchMapping
    public void deleteDir(@AuthenticationPrincipal AuthedUserDetail userDetail, @RequestBody DirDto.Request request) {
        //디렉토리 삭제
        directoryService.deleteDirSoft(userDetail.getUserSeq(), request);
        //하위 디렉토리 큐 등록
        directoryService.inDeleteQueue(userDetail.getUserSeq(), request.getDirSeq());
    }
}
