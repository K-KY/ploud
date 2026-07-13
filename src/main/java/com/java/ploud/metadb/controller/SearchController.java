package com.java.ploud.metadb.controller;

import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.metadb.service.SearchService;
import com.java.ploud.metadb.service.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public SearchDto.Response search(
            @AuthenticationPrincipal AuthedUserDetail userDetail,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer limit
    ) {
        String searchKeyword = keyword == null ? q : keyword;
        return searchService.search(userDetail.getUserSeq(), searchKeyword, limit);
    }
}
