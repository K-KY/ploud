package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.dto.FileUploadDto;
import com.java.ploud.metadb.service.entity.Files;
import com.java.ploud.metadb.service.repository.FileRepository;
import io.minio.ObjectWriteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class FileService {
    private final FileRepository fileRepository;
    private final DirectoryService directoryService;

    public FileService(FileRepository fileRepository, DirectoryService directoryService) {
        this.fileRepository = fileRepository;
        this.directoryService = directoryService;
    }


    /**
     * @apiNote  - 폴더로 업로드 시 폴더 이름이 원본 이름에 포함되는 현상
     * @param multipartFile
     * @param ownerId
     * @param upload
     * @return
     */
    public Files upload(MultipartFile multipartFile, String ownerId, ObjectWriteResponse upload) {

        //이거를 특정 구분자로 나누고 나눠진 문자열로 dir테이블에 저장
        //소유자에게 이미 해당하는 경로가 있으면 제외
        String originalFilename = multipartFile.getOriginalFilename();

        Files entity = Files.builder()
                .ownerSeq(ownerId)
                .title(originalFilename)
                .storageKey(upload.object())
                .originalFilename(multipartFile.getOriginalFilename())
                .size(multipartFile.getSize())
                .contentType(multipartFile.getContentType())
                .parent(directoryService.findLastParent(originalFilename, ownerId))
                .build();

        return fileRepository.save(entity);
    }

    public List<Files> readFiles(Long location) {
        return fileRepository.findByParent_DirSeq(location);
    }
}
