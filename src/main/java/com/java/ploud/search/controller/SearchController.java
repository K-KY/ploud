package com.java.ploud.search.controller;

import com.java.ploud.auth.dto.AuthedUserDetail;
import com.java.ploud.search.dto.SearchRequestDto;
import com.java.ploud.search.dto.SearchResultDto;
import com.java.ploud.search.service.SearchFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SearchFacade facade;

    @GetMapping
    public SearchResultDto search(@AuthenticationPrincipal AuthedUserDetail userDetail, SearchRequestDto searchRequestDto) {
        return facade.search(userDetail.getUserSeq(), searchRequestDto);
    }
}
