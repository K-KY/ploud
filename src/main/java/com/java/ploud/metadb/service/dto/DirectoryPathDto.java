package com.java.ploud.metadb.service.dto;

public class DirectoryPathDto {
    private Long dirSeq;
    private Long parentDirSeq;
    private String dirName;
    private Long depth;

    public DirectoryPathDto() {
    }

    public DirectoryPathDto(Long dirSeq, Long parentDirSeq, String dirName, Long depth) {
        this.dirSeq = dirSeq;
        this.parentDirSeq = parentDirSeq;
        this.dirName = dirName;
        this.depth = depth;
    }

    public Long getDirSeq() {
        return dirSeq;
    }

    public Long getParentDirSeq() {
        return parentDirSeq;
    }

    public String getDirName() {
        return dirName;
    }

    public Long getDepth() {
        return depth;
    }
}
