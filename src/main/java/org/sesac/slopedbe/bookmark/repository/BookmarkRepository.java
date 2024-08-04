package org.sesac.slopedbe.bookmark.repository;

import java.util.List;

import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.bookmark.model.entity.BookmarkId;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.springframework.data.repository.CrudRepository;

public interface BookmarkRepository extends CrudRepository<Bookmark, BookmarkId> {
	List<Bookmark> findByMemberId(MemberCompositeKey id);
}
