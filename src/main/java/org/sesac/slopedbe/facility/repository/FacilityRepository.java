package org.sesac.slopedbe.facility.repository;

import java.util.List;
import java.util.Optional;

import org.sesac.slopedbe.facility.model.dto.vo.FacilityVO;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FacilityRepository extends CrudRepository<Facility, Long> {

	@Query(value = "SELECT " +
		"f.id, f.name, f.address, f.facility_type as type, " +
		"ST_Y(f.point::geometry) as latitude, " +
		"ST_X(f.point::geometry) as longitude, " +
		"(SELECT COUNT(*) FROM facility_review fr WHERE fr.facility_id = f.id) as count_of_reviews, " +
		"(SELECT COUNT(*) FROM facility_review fr WHERE fr.facility_id = f.id AND fr.is_convenient = true) as count_of_convenient, " +
		"(SELECT COUNT(*) FROM facility_review fr WHERE fr.facility_id = f.id AND fr.is_convenient = false) as count_of_inconvenient, " +
		"(SELECT fri.url FROM facility_review_image fri " +
		"JOIN facility_review fr ON fri.facility_review_id = fr.id " +
		"WHERE fr.facility_id = f.id " +
		"ORDER BY fri.created_at DESC LIMIT 1) as image_url " +
		"FROM facility f " +
		"WHERE f.id = :facilityId",
		nativeQuery = true)
	Optional<FacilityVO> findFacilityDtoById(@Param("facilityId") Long facilityId);

	@Query(value = "SELECT " +
		"f.id, f.name, f.address, f.facility_type as type, " +
		"ST_Y(f.point::geometry) as latitude, " +
		"ST_X(f.point::geometry) as longitude, " +
		"(SELECT COUNT(*) FROM facility_review fr WHERE fr.facility_id = f.id) as count_of_reviews, " +
		"(SELECT COUNT(*) FROM facility_review fr WHERE fr.facility_id = f.id AND fr.is_convenient = true) as count_of_convenient, " +
		"(SELECT COUNT(*) FROM facility_review fr WHERE fr.facility_id = f.id AND fr.is_convenient = false) as count_of_inconvenient, " +
		"(SELECT fri.url FROM facility_review_image fri " +
		"JOIN facility_review fr ON fri.facility_review_id = fr.id " +
		"WHERE fr.facility_id = f.id " +
		"ORDER BY fri.created_at DESC LIMIT 1) as image_url " +
		"FROM facility f " +
		"WHERE ST_DWithin(f.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :distance) " +
		"ORDER BY ST_Distance(f.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) " +
		"LIMIT :limit",
		nativeQuery = true)
	List<FacilityVO> findNearbyFacilitiesDetailed(@Param("latitude") double latitude,
		@Param("longitude") double longitude,
		@Param("distance") double distanceInMeters,
		@Param("limit") int limit);
}
