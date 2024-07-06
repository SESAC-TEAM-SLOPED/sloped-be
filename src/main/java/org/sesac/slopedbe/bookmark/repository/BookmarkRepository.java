package org.sesac.slopedbe.bookmark.repository;

import java.util.List;

import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.bookmark.model.entity.BookmarkId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends CrudRepository<Bookmark, BookmarkId> {
	@Query("SELECT b FROM Bookmark b WHERE b.member.email = :email")
	List<Bookmark> findByEmail(@Param("email") String email);
}
