package com.java.ploud.exceptions;

public class DirectoryNotFoundException extends RuntimeException {

    public DirectoryNotFoundException(Long dirSeq) {
        super("Directory not found for dirSeq=" + dirSeq);
    }
}
