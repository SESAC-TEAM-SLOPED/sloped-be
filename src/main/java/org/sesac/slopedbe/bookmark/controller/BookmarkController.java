package org.sesac.slopedbe.bookmark.controller;

import java.io.IOException;
import java.util.List;

import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.bookmark.model.dto.BookmarkRequestDTO;
import org.sesac.slopedbe.bookmark.model.dto.BookmarkResponseDTO;
import org.sesac.slopedbe.bookmark.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RequestMapping("/api/users/bookmark")
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final JwtUtil jwtUtil;

    @GetMapping("/")
    public ResponseEntity<List<BookmarkResponseDTO>> getBookmarksByUserEmail(@RequestHeader("x-access-token") String token) {
        String email = jwtUtil.extractUsername(token);
        List<BookmarkResponseDTO> bookmark = bookmarkService.getBookmarksByMemberEmail(email);
        return ResponseEntity.ok(bookmark);
    }

    @PostMapping("/")
    public ResponseEntity<Void> addBookmark(@RequestHeader("x-access-token") String token, @ModelAttribute
        BookmarkRequestDTO bookmarkRequestDTO) throws IOException {
        String email = jwtUtil.extractUsername(token);
        bookmarkService.addBookmark(email, bookmarkRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> removeBookmark(@RequestHeader("x-access-token") String token, @ModelAttribute
    BookmarkRequestDTO bookmarkRequestDTO) {
        String email = jwtUtil.extractUsername(token);
        bookmarkService.removeBookmark(email, bookmarkRequestDTO);
        return ResponseEntity.noContent().build();
    }
}
