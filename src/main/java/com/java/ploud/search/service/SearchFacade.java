package com.java.ploud.search.service;

import com.java.ploud.metadb.service.DirectoryService;
import com.java.ploud.metadb.service.FileService;
import com.java.ploud.metadb.service.dto.DirDto;
import com.java.ploud.metadb.service.dto.FileDto;
import com.java.ploud.search.dto.SearchRequestDto;
import com.java.ploud.search.dto.SearchResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchFacade {
    private final FileService fileService;
    private final DirectoryService directoryService;

    public SearchResultDto search(Long userSeq, SearchRequestDto request) {
        if (request == null) {
            return null;
        }
        if (request.getOnlyDirs() && request.getOnlyFiles()) {
            SearchResultDto searchResultDto = new SearchResultDto(
                    searchFile(request.getKeyword(), userSeq),
                    searchDir(request.getKeyword(), userSeq)
            );
        }
        if (request.getOnlyFiles() && !request.getOnlyDirs()) {
            SearchResultDto searchResultDto = new SearchResultDto(
                    searchFile(request.getKeyword(), userSeq), null);
        }
        if (request.getOnlyDirs() && !request.getOnlyFiles()) {
            SearchResultDto searchResultDto = new SearchResultDto(
                    null, searchDir(request.getKeyword(), userSeq));
        }
        return new SearchResultDto(null, null);
    }


    //file
    public List<FileDto.Response> searchFile(String keyword, Long userSeq) {
        return null;
    }
    //dir
    public List<DirDto.Response> searchDir(String keyword, Long userSeq) {
        return null;
    }
}
