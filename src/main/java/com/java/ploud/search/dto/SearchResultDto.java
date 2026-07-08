package com.java.ploud.search.dto;

import com.java.ploud.metadb.service.dto.DirDto;
import com.java.ploud.metadb.service.dto.FileDto;
import lombok.Getter;

import java.util.List;

@Getter
public class SearchResultDto {
    public SearchResultDto(List<FileDto.Response> files, List<DirDto.Response> dirs) {
        this.files = files;
        this.dirs = dirs;
    }

    private List<FileDto.Response> files;
    private List<DirDto.Response> dirs;
}
