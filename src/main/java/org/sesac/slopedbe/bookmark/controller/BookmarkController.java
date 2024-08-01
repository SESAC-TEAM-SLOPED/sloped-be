package org.sesac.slopedbe.bookmark.controller;

import java.util.List;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.bookmark.model.dto.request.FacilityTestDTO;
import org.sesac.slopedbe.bookmark.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users/bookmark")
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("")
    public ResponseEntity<List<FacilityTestDTO>> getBookmark(@RequestParam Long facilityId,
        @AuthenticationPrincipal GeneralUserDetails userDetails) {
        log.info("bb userDetails: {}", userDetails.getMember().getId().getEmail());
        return ResponseEntity.ok(bookmarkService.getBookmarks(facilityId));
    }

}
