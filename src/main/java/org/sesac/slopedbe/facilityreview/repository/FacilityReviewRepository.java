package org.sesac.slopedbe.facilityreview.repository;

import java.util.List;

import org.sesac.slopedbe.facilityreview.model.entity.FacilityReview;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FacilityReviewRepository extends CrudRepository<FacilityReview, Long> {
	@Query(value = "SELECT fri.url " +
		"FROM facility_review_image fri " +
		"JOIN facility_review fr ON fri.facility_review_id = fr.id " +
		"WHERE fr.facility_id = :facilityId " +
		"ORDER BY fr.created_at DESC, fri.created_at DESC " +
		"LIMIT 3", nativeQuery = true)
	List<String> findTop3ReviewImageUrlsByFacilityId(Long facilityId);

	@Query("SELECT fr FROM FacilityReview fr WHERE fr.member.id = :id")
	List<FacilityReview> findByMemberId(@Param("id") MemberCompositeKey id);
}
