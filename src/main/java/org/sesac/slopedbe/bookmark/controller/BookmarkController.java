package org.sesac.slopedbe.bookmark.controller;

import java.util.List;

import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.bookmark.model.dto.BookmarkRequestDTO;
import org.sesac.slopedbe.bookmark.model.dto.BookmarkResponseDTO;
import org.sesac.slopedbe.bookmark.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RequestMapping("/api/users/bookmark")
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "회원별 즐겨찾기 조회", description = "회원의 email로 즐겨찾기 목록을 조회한다.")
    @ApiResponse(responseCode = "200", description = "즐겨찾기 조회 성공", content = @Content(schema = @Schema(implementation = BookmarkResponseDTO.class)))
    @GetMapping("/")
    public ResponseEntity<List<BookmarkResponseDTO>> getBookmarksByUserEmail(@RequestHeader("x-access-token") String token) {
        String email = jwtUtil.extractEmailFromToken(token.substring(7));
        List<BookmarkResponseDTO> bookmark = bookmarkService.getBookmarksByEmail(email);
        return ResponseEntity.ok(bookmark);
    }

    @Operation(summary = "즐겨찾기 추가", description = "token과 시설 ID로 즐겨찾기를 추가한다.")
    @ApiResponse(responseCode = "201", description = "즐겨찾기 추가 성공")
    @ApiResponse(responseCode = "400", description = "존재하지 않는 시설입니다.")
    @ApiResponse(responseCode = "409", description = "해당 시설은 이미 회원의 즐겨찾기 목록에 있습니다.")
    @PostMapping("/")
    public ResponseEntity<Void> addBookmark(@RequestHeader("x-access-token") String token, @RequestBody
    BookmarkRequestDTO bookmarkRequestDTO) {
        String email = jwtUtil.extractEmailFromToken(token.substring(7));
        bookmarkService.addBookmark(email, bookmarkRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "즐겨찾기 삭제", description = "token과 시설 ID로 즐겨찾기를 삭제한다.")
    @ApiResponse(responseCode = "204", description = "즐겨찾기 삭제 성공")
    @ApiResponse(responseCode = "400", description = "존재하지 않는 시설입니다.")
    @DeleteMapping("/")
    public ResponseEntity<Void> removeBookmark(@RequestHeader("x-access-token") String token, @RequestBody
    BookmarkRequestDTO bookmarkRequestDTO) {
        String email = jwtUtil.extractEmailFromToken(token.substring(7));
        bookmarkService.removeBookmark(email, bookmarkRequestDTO);
        return ResponseEntity.noContent().build();
    }
}
