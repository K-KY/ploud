package com.java.ploud.metadb.controller;

import com.java.ploud.metadb.service.DirectoryService;
import com.java.ploud.metadb.service.dto.DirDto;
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
        return directoryService.findDir(request.ownerId, request.parentSeq)
                .stream().map(d -> DirDto.Response
                        .builder()
                        .dirSeq(d.getDirSeq())
                        .ownerId(d.getOwnerId())
                        .dirName(d.getDirName())
                        .parentSeq(d.getParentSeq())
                        .build()).toList();
    }
}
