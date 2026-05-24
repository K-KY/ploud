package com.java.ploud.metadb.service.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ExploreDto {
    private List<DirDto.Response> dirs;
    private String path;


    public List<DirDto.Response> getDirs() {
        return dirs;
    }

    public String getPath() {
        return path;
    }
}
