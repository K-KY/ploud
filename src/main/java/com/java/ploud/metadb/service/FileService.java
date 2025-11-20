package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.entity.Files;
import com.java.ploud.metadb.service.repository.FileRepository;
import io.minio.ObjectWriteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileService {
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public Files upload(MultipartFile multipartFile, String ownerId, ObjectWriteResponse upload) {

        Files entity = Files.builder()
                .ownerSeq(ownerId)
                .title(multipartFile.getOriginalFilename())
                .storageKey(upload.object())
                .originalFilename(multipartFile.getOriginalFilename())
                .size(multipartFile.getSize())
                .contentType(multipartFile.getContentType())
                .build();

        return fileRepository.save(entity);
    }

}
