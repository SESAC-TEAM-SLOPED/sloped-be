package org.sesac.slopedbe.bookmark.repository;

import java.util.List;

import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.bookmark.model.entity.BookmarkId;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends CrudRepository<Bookmark, BookmarkId> {
	@Query("SELECT b FROM Bookmark b WHERE b.member.id = :id")
	List<Bookmark> findByMemberId(@Param("id")MemberCompositeKey id);
	List<Bookmark> findByMember(Member member);
	boolean existsByMember_Id_EmailAndMember_Id_OauthTypeAndFacility_Id(
		String email, MemberOauthType oauthType, Long facilityId
	);
}
