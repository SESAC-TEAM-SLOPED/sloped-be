package org.sesac.slopedbe.bookmark.repository;

import java.util.List;

import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.bookmark.model.entity.BookmarkId;
import org.springframework.data.repository.CrudRepository;

public interface BookmarkRepository extends CrudRepository<Bookmark, BookmarkId> {
	List<Bookmark> findByEmail(String email);
}
