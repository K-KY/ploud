package com.java.ploud.metadb.service.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ExploreDto {
    private List<DirDto.Response> dirs;
    private String key;
    private String path;
    private Long current;


    public List<DirDto.Response> getDirs() {
        return dirs;
    }

    public String getKey() {
        return key;
    }

    public String getPath() {
        return path;
    }

    public Long getCurrent() {
        return current;
    }
}
