package org.sesac.slopedbe.bookmark.service;

import java.util.List;

import org.sesac.slopedbe.bookmark.model.entity.Bookmark;

public interface BookmarkService {
	void addBookmark(String email, Long facilityId);
	void removeBookmark(String email, Long facilityId);
	List<Bookmark> getBookmarksByMemberEmail(String email);

}
