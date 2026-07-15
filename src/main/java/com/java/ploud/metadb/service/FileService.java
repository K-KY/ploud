package com.java.ploud.metadb.service;

import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.auth.entity.User;
import com.java.ploud.auth.service.UserService;
import com.java.ploud.metadb.service.dto.FileChangeDto;
import com.java.ploud.metadb.service.dto.FileDto;
import com.java.ploud.metadb.service.dto.MetaDataDto;
import com.java.ploud.metadb.service.entity.Directory;
import com.java.ploud.metadb.service.entity.Files;
import com.java.ploud.metadb.service.entity.TargetTypes;
import com.java.ploud.metadb.service.repository.FileRepository;
import com.java.ploud.storage.service.dto.FileDeleteDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final DirectoryService directoryService;
    private final UserService userService;
    private final DeleteQueueService deleteQueueService;
    private final StorageCleanService storageCleanService;

    /**
     * @param userDetail
     * @param metaDataDto
     * @return
     * @apiNote - 폴더로 업로드 시 폴더 이름이 원본 이름에 포함되는 현상
     */
    @Transactional
    public Files upload(AuthedUserDetail userDetail, MetaDataDto metaDataDto) {
        //이거를 특정 구분자로 나누고 나눠진 문자열로 dir테이블에 저장
        //소유자에게 이미 해당하는 경로가 있으면 제외
        //metaDataDto.getETag(); --> 해당 데이터로 완전히 같은 파일 중복 확인
        Files file = buildFile(userDetail, metaDataDto);
        fileRepository.save(file);
        return file;
    }

    @Transactional
    public FileDto.Response uploadResponse(AuthedUserDetail userDetail, MetaDataDto metaDataDto) {
        Files file = buildFile(userDetail, metaDataDto);
        Files saved = fileRepository.save(file);
        return FileDto.Response.from(saved);
    }

    public Files buildFile(AuthedUserDetail userDetail, MetaDataDto metaDataDto) {
        String originalFilename = metaDataDto.getOriginalFilename();
        User user = userService.findByUserSeq(userDetail.getUserSeq());

        //중복되는 파일 찾기
        Files duplicate = findDuplicate(metaDataDto.getFileHash());
        String storageKey = metaDataDto.getStorageKey(); // 기본값으로 초기화
        if (duplicate != null) {// 조회한 중복 데이터가 null이 아니면 조회된 값의 storage key 사용
            log.info("Duplicate storage key: {}", storageKey);
            log.info("enqueue duplicate storage key: {}", metaDataDto.getStorageKey());
            storageCleanService.save(user.getUserSeq(), storageKey);//큐 등록
            storageKey = duplicate.getStorageKey();//이미 존재하는 경로 사용
        }


        return Files.builder()
                .user(user)
                .title(Paths.get(originalFilename).getFileName().toString())
                .storageKey(storageKey)
                .originalFilename(originalFilename)
                .size(metaDataDto.getSize())
                .contentType(metaDataDto.getContentType())
                .parent(getLastParent(user.getUserSeq(), metaDataDto.getLocation() + originalFilename))
                .fileHash(metaDataDto.getFileHash())
                //todo 존재하는 데이터를 다시 확인중임 프론트에서 현재 경로 pk를 같이 받아서 없는 경로부터 확인하도록 최적화
                .build();

    }

    private Files findDuplicate(String fileHash) {
        List<Files> duplicates = fileRepository.findByFileHash(fileHash);
        //진짜로 이 경로의 파일이 존재하는지 확인
        log.info("found duplicates: {}", duplicates.size());
        if (duplicates.isEmpty()) {
            return null;
        }
        return duplicates.getFirst();
    }

    private Directory getLastParent(Long userSeq, String originalFilename) {
        return directoryService.findOrCreateLastParent(originalFilename, userSeq);
    }

    public List<Files> readFiles(Long location) {
        return fileRepository.findByParent_DirSeq(location);
    }

    public List<Files> readFiles(Long userSeq, Long dirSeq) {
        if (dirSeq == null) {
            return getRootFiles(userSeq);
        }
        return fileRepository.findByUser_UserSeqAndParent_DirSeqAndDeletedFalse(userSeq, dirSeq);
    }

    public List<Files> getRootFiles(Long userSeq) {
        return fileRepository.findByUser_UserSeqAndParent_DirSeqAndDeletedFalse(userSeq, directoryService.findRoot(userSeq).getDirSeq());
    }

    public Directory createRoot(Long userSeq) {
        return directoryService.createRoot(userSeq);
    }

    public void deleteFile(Long userSeq, FileDeleteDto request) {
        fileRepository.deleteByUser_userSeqAndFileSeq(userSeq, request.getFileId());
    }

    @Transactional
    public void deleteFileSoft(Long userSeq, Long fileSeq) {
        //현재 파일 삭제
        Files file = fileRepository.findByUser_UserSeqAndFileSeq(userSeq, fileSeq);
        file.delete();
    }

    public void inDeleteQueue(Long userSeq, Long fileSeq) {
        deleteQueueService.save(userSeq, fileSeq, TargetTypes.DEL_FILE);
    }

    public List<Files> changeDir(Long userSeq, FileChangeDto.Request dto) {

        Directory dir = directoryService.findDir(userSeq, dto.getTargetDirSeq());
        List<FileDto.Request> moveFiles = dto.getFiles();
        List<Files> files = moveFiles.stream()
                .map(mf -> fileRepository.findByUser_UserSeqAndFileSeq(userSeq, mf.getDirSeq()))
                .filter(Objects::nonNull)
                .toList();
        files.forEach(f -> f.changeDir(dir));
        return files;
    }
}
