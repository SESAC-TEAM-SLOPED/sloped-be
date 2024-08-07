package org.sesac.slopedbe.bookmark.service;

import java.util.List;

import org.sesac.slopedbe.bookmark.model.dto.BookmarkRequestDTO;
import org.sesac.slopedbe.bookmark.model.dto.BookmarkResponseDTO;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;

public interface BookmarkService {
	void addBookmark(MemberCompositeKey memberCompositeKey, BookmarkRequestDTO bookmarkRequestDTO);
	void removeBookmark(MemberCompositeKey memberCompositeKey, BookmarkRequestDTO bookmarkRequestDTO);
	List<BookmarkResponseDTO> getBookmarksById(MemberCompositeKey memberCompositeKey);
}
