package org.sesac.slopedbe.bookmark.controller;

import java.util.List;

import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.bookmark.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RequestMapping("/api/users/bookmark")
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/")
    public ResponseEntity<List<Bookmark>> getBookmarksByUserEmail(@RequestHeader("x-access-token") String token) {
        List<Bookmark> bookmark = bookmarkService.getBookmarksByMemberEmail(token);
        return ResponseEntity.ok(bookmark);
    }

    @PostMapping("/")
    public ResponseEntity<Void> addBookmark(@RequestHeader("x-access-token") String token, @RequestBody Long facilityId) {
        bookmarkService.addBookmark(token, facilityId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> removeBookmark(@RequestHeader("x-access-token") String token, @RequestBody Long facilityId) {
        bookmarkService.removeBookmark(token, facilityId);
        return ResponseEntity.noContent().build();
    }
}
