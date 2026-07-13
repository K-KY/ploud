package com.java.ploud.metadb.service;

import com.java.ploud.metadb.service.dto.DirectoryDto;
import com.java.ploud.metadb.service.dto.FileDto;
import com.java.ploud.metadb.service.dto.SearchDto;
import com.java.ploud.metadb.service.repository.DirectoryRepository;
import com.java.ploud.metadb.service.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 100;

    private final DirectoryRepository directoryRepository;
    private final FileRepository fileRepository;

    @Transactional(readOnly = true)
    public SearchDto.Response search(Long userSeq, String keyword, Integer limit) {
        String normalizedKeyword = normalize(keyword);
        int normalizedLimit = normalizeLimit(limit);

        if (normalizedKeyword.isBlank()) {
            return SearchDto.Response.builder()
                    .keyword(normalizedKeyword)
                    .limit(normalizedLimit)
                    .dirs(List.of())
                    .files(List.of())
                    .build();
        }

        PageRequest pageRequest = PageRequest.of(0, normalizedLimit);

        List<DirectoryDto.Response> dirs = directoryRepository
                .searchDirectories(userSeq, normalizedKeyword, pageRequest)
                .stream()
                .map(DirectoryDto.Response::from)
                .toList();

        List<FileDto.Response> files = fileRepository
                .searchFiles(userSeq, normalizedKeyword, pageRequest)
                .stream()
                .map(FileDto.Response::from)
                .toList();

        return SearchDto.Response.builder()
                .keyword(normalizedKeyword)
                .limit(normalizedLimit)
                .dirs(dirs)
                .files(files)
                .build();
    }

    private String normalize(String keyword) {
        return keyword == null ? "" : keyword.trim();
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        return Math.min(Math.max(limit, 1), MAX_LIMIT);
    }
}
