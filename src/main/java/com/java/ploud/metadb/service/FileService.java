package com.java.ploud.metadb.service;

import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.auth.entity.User;
import com.java.ploud.auth.service.UserService;
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
    private final UserService userService;

    public FileService(FileRepository fileRepository, DirectoryService directoryService, UserService userService) {
        this.fileRepository = fileRepository;
        this.directoryService = directoryService;
        this.userService = userService;
    }


    /**
     * @param userDetail
     * @param metaDataDto
     * @return
     * @apiNote - 폴더로 업로드 시 폴더 이름이 원본 이름에 포함되는 현상
     */
    public Files upload(AuthedUserDetail userDetail, MetaDataDto metaDataDto) {
        //이거를 특정 구분자로 나누고 나눠진 문자열로 dir테이블에 저장
        //소유자에게 이미 해당하는 경로가 있으면 제외
        String originalFilename = metaDataDto.getOriginalFilename();
        String location = metaDataDto.getLocation();
        User user = userService.findByUserSeq(userDetail.getUserSeq());

        Files entity = Files.builder()
                .user(user)
                .title(Paths.get(originalFilename).getFileName().toString())
                .storageKey(user.getUserSeq() + "/" + location + originalFilename)
                .originalFilename(originalFilename)
                .size(metaDataDto.getSize())
                .contentType(metaDataDto.getContentType())
                .parent(getLastParent(user.getUserSeq(), location + originalFilename))
                //todo 존재하는 데이터를 다시 확인중임 프론트에서 현재 경로 pk를 같이 받아서 없는 경로부터 확인하도록 최적화
                .build();

        return fileRepository.save(entity);
    }

    private Directory getLastParent(Long userSeq, String originalFilename) {
        return directoryService.findOrCreateLastParent(originalFilename, userSeq);
    }

    public List<Files> readFiles(Long location) {
        return fileRepository.findByParent_DirSeq(location);
    }

    public List<Files> readFiles(Long userSeq, Long parentDirSeq) {
        if (parentDirSeq == null) {
            return fileRepository.findByUser_UserSeqAndParent_DirSeq(userSeq, directoryService.findRoot(userSeq).getDirSeq());
        }
        return fileRepository.findByUser_UserSeqAndParent_DirSeq(userSeq, parentDirSeq);
    }

    public Directory createRoot(Long userSeq) {
        return directoryService.createRoot(userSeq);
    }
}
