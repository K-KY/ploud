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

    public String encryptKey(String path) {
        try {
            return encryptor.encrypt(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String encryptKey(List<Long> path) {
        StringJoiner joiner = new StringJoiner("/");
        path.forEach(p -> joiner.add(p.toString()));
        try {
            return encryptor.encrypt(joiner.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String encryptPath(List<String> path) {
        StringJoiner joiner = new StringJoiner("/");
        path.forEach(joiner::add);
        try {
            return encryptor.encrypt(joiner.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long[] decryptKey(String key) {
        try {
            return Arrays.stream(encryptor.decrypt(key).split("/")).mapToLong(Long::parseLong).toArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String[] decryptPath(String path) {
        try {
            return (String[]) Arrays.stream(encryptor.decrypt(path).split("/")).toArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decryptString(String path) {
        try {
            return encryptor.decrypt(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
