package com.java.ploud.search.dto;

import lombok.Getter;

@Getter
public class SearchRequestDto {
    private String keyword;
    private Boolean onlyFiles;
    private Boolean onlyDirs;
}
