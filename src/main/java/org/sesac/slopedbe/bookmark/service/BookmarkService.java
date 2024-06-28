package org.sesac.slopedbe.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.bookmark.repository.BookmarkRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

}
