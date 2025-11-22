package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.entity.Files;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class FileServiceTest {

    @Autowired
    private FileService fileService;

    @Test
    void test() {
        List<Files> files = fileService.readFiles(1L);

        System.out.println("files = " + files);
    }

}