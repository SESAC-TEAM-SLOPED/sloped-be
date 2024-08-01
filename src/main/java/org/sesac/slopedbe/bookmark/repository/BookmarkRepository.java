package org.sesac.slopedbe.bookmark.repository;

import java.util.List;

import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.bookmark.model.entity.BookmarkId;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface BookmarkRepository extends CrudRepository<Bookmark, BookmarkId> {
	boolean existsByMember_Id_EmailAndMember_Id_OauthTypeAndFacility_Id(
		String email, MemberOauthType oauthType, Long facilityId
	);

	@Query("SELECT b FROM Bookmark b WHERE b.facility.id = :facilityId")
	List<Bookmark> findAllByFacilityId(Long facilityId);
}
