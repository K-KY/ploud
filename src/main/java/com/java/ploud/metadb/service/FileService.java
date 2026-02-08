package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.dto.MetaDataDto;
import com.java.ploud.metadb.service.entity.Directory;
import com.java.ploud.metadb.service.entity.Files;
import com.java.ploud.metadb.service.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
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
     * @param metaDataDto
     * @return
     * @apiNote - 폴더로 업로드 시 폴더 이름이 원본 이름에 포함되는 현상
     */
    public Files upload(MetaDataDto metaDataDto) {
        //이거를 특정 구분자로 나누고 나눠진 문자열로 dir테이블에 저장
        //소유자에게 이미 해당하는 경로가 있으면 제외
        String originalFilename = metaDataDto.getOriginalFilename();
        String location = metaDataDto.getLocation();

        Files entity = Files.builder()
                .ownerSeq(metaDataDto.getOwnerId())
                .title(Paths.get(originalFilename).getFileName().toString())
                .storageKey(metaDataDto.getOwnerId() + "/" + location + originalFilename)
                .originalFilename(originalFilename)
                .size(metaDataDto.getSize())
                .contentType(metaDataDto.getContentType())
                .parent(getLastParent(metaDataDto.getOwnerId(), location + originalFilename))
                //todo 존재하는 데이터를 다시 확인중임 프론트에서 현재 경로 pk를 같이 받아서 없는 경로부터 확인하도록 최적화
                .build();

        return fileRepository.save(entity);
    }

    private Directory getLastParent(String ownerId, String originalFilename) {
        return directoryService.findOrCreateLastParent(originalFilename, ownerId);
    }

    public List<Files> readFiles(Long location) {
        return fileRepository.findByParent_DirSeq(location);
    }

    public List<Files> readFiles(String ownerId, Long parentDirSeq) {
        return fileRepository.findByOwnerSeqAndParent_DirSeq(ownerId, parentDirSeq);
    }

    public Directory createRoot(String ownerId) {
        return directoryService.createRoot(ownerId);
    }
}
