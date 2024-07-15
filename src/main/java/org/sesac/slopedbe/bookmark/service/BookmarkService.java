package org.sesac.slopedbe.bookmark.service;

import java.io.IOException;
import java.util.List;

import org.sesac.slopedbe.bookmark.model.dto.BookmarkRequestDTO;
import org.sesac.slopedbe.bookmark.model.dto.BookmarkResponseDTO;

public interface BookmarkService {
	void addBookmark(String email, BookmarkRequestDTO bookmarkRequestDTO) throws IOException;
	void removeBookmark(String email, BookmarkRequestDTO bookmarkRequestDTO);
	List<BookmarkResponseDTO> getBookmarksByMemberEmail(String email);

}
