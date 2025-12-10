package com.java.ploud.metadb.controller;

import com.java.ploud.metadb.service.DirectoryService;
import com.java.ploud.metadb.service.dto.DirDto;
import com.java.ploud.metadb.service.entity.Directory;
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
    public List<DirDto.Response> getDir(@RequestBody DirDto.Request request) {
        return directoryService.findChildDir(request.getOwnerId(), request.getParentSeq())
                .stream().map(d -> DirDto.Response
                        .builder()
                        .dirSeq(d.getDirSeq())
                        .ownerId(d.getOwnerId())
                        .dirName(d.getDirName())
                        .parentSeq(d.getParentSeq())
                        .build()).toList();
    }

    @PostMapping("current")
    public DirDto.Response getCurrent(@RequestBody DirDto.Request request) {

        Directory parentDir = directoryService.findDir(request.getOwnerId(), request.getParentSeq());

        return DirDto.Response.builder()
                .dirSeq(parentDir.getDirSeq())
                .dirName(parentDir.getDirName())
                .parentSeq(parentDir.getParentSeq())
                .ownerId(parentDir.getOwnerId())
                .build();
    }
}
