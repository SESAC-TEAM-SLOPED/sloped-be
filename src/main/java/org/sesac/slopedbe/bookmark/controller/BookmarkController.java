package org.sesac.slopedbe.bookmark.controller;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.bookmark.service.BookmarkService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RequestMapping("/api/users/bookmark")
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

}
