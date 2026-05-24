package com.java.ploud.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Component
public class PathEncryptor {
    private final Encryptor encryptor;

    public PathEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    public String encrypt(String path) {
        try {
            return encryptor.encrypt(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String encrypt(List<Long> path) {
        StringJoiner joiner = new StringJoiner("/");
        path.forEach(p -> joiner.add(p.toString()));
        try {
            return encryptor.encrypt(joiner.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long[] decrypt(String path) {
        try {
            return Arrays.stream(encryptor.decrypt(path).split("/")).mapToLong(Long::parseLong).toArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
