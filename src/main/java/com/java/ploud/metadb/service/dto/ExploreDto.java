package com.java.ploud.metadb.service.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ExploreDto {
    private List<DirectoryDto.Response> dirs;
    private Long current;


    public List<DirectoryDto.Response> getDirs() {
        return dirs;
    }

    public Long getCurrent() {
        return current;
    }
}
