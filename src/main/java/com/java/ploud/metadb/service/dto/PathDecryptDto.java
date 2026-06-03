package com.java.ploud.metadb.service.dto;

public class PathDecryptDto {
    private String key;
    private String path;

    public PathDecryptDto(String key, String path) {
        this.key = key;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getKey() {
        return key;
    }
}
