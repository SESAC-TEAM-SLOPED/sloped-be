package org.sesac.slopedbe.bookmark.service;

import java.util.List;

import org.sesac.slopedbe.bookmark.model.dto.BookmarkRequestDTO;
import org.sesac.slopedbe.bookmark.model.dto.BookmarkResponseDTO;

public interface BookmarkService {
	void addBookmark(String email,
		BookmarkRequestDTO bookmarkRequestDTO);
	void removeBookmark(String email, BookmarkRequestDTO bookmarkRequestDTO);
	List<BookmarkResponseDTO> getBookmarksByEmail(String email);

}
