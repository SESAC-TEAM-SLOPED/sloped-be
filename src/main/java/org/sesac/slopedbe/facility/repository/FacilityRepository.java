package org.sesac.slopedbe.facility.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.facility.model.vo.FacilityScoreVO;
import org.sesac.slopedbe.facility.model.vo.FacilitySimpleVO;
import org.sesac.slopedbe.facility.model.vo.FacilityVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.annotation.Nullable;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

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
	Optional<FacilityVO> findFacilityById(@Param("facilityId") Long facilityId);

	@Query(value = "SELECT " +
		"f.id, f.name, f.address, f.facility_type as type, " +
		"ST_Y(f.point::geometry) as latitude, " +
		"ST_X(f.point::geometry) as longitude, " +
		"(SELECT COUNT(*) FROM facility_review fr WHERE fr.facility_id = f.id) as count_of_reviews, " +
		"(SELECT COUNT(*) FROM facility_review fr "
			+ "WHERE fr.facility_id = f.id AND fr.is_convenient = true) as count_of_convenient, " +
			"(SELECT COUNT(*) FROM facility_review fr "
			+ "WHERE fr.facility_id = f.id AND fr.is_convenient = false) as count_of_inconvenient, " +
		"(SELECT fri.url FROM facility_review_image fri " +
		"JOIN facility_review fr ON fri.facility_review_id = fr.id " +
		"WHERE fr.facility_id = f.id " +
		"ORDER BY fri.created_at DESC LIMIT 1) as image_url " +
		"FROM facility f " +
		"WHERE ST_DWithin(f.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :distance) " +
		"AND (CASE WHEN :facilityType IS NULL THEN true ELSE f.facility_type = :facilityType END) " +
		"ORDER BY ST_Distance(f.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) " +
		"LIMIT :limit",
		nativeQuery = true)
	List<FacilityVO> findNearbyFacilities(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("distance") double distance, @Param("limit") int limit, @Param("facilityType") @Nullable String facilityType);

	@Query(value = """
        SELECT f.id, f.name, f.address, f.facility_type as type,
            ST_Y(f.point::geometry) as latitude,
            ST_X(f.point::geometry) as longitude,
            (SELECT COUNT(*) FROM facility_review fr WHERE fr.facility_id = f.id) as count_of_reviews,
            (SELECT COUNT(*) FROM facility_review fr 
             WHERE fr.facility_id = f.id AND fr.is_convenient = true) as count_of_convenient,
            (SELECT COUNT(*) FROM facility_review fr 
             WHERE fr.facility_id = f.id AND fr.is_convenient = false) as count_of_inconvenient,
            (SELECT fri.url FROM facility_review_image fri 
             JOIN facility_review fr ON fri.facility_review_id = fr.id 
             WHERE fr.facility_id = f.id 
             ORDER BY fri.created_at DESC LIMIT 1) as image_url,
            f.average_accessibility_score,
            f.accessibility_description
        FROM facility f
        WHERE ST_DWithin(f.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :distance)
        ORDER BY f.average_accessibility_score DESC NULLS LAST,
                 ST_Distance(f.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326))
        LIMIT 10
        """, nativeQuery = true)
	List<FacilityScoreVO> findNearbyFacilitiesSortedByAccessibilityScore(
		@Param("latitude") double latitude,
		@Param("longitude") double longitude,
		@Param("distance") double distance
	);

	@Query(value = "SELECT f.id, f.name, f.facility_type as type, f.address, " +
		"ST_Distance(f.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography) as distance_meters " +
		"FROM facility f " +
		"WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
		"ORDER BY distance_meters ASC " +
		"LIMIT 20",
		nativeQuery = true)
	List<FacilitySimpleVO> findByNameWithDistance(@Param("name") String name, @Param("latitude") double latitude, @Param("longitude") double longitude);

	@Query(value = "INSERT INTO facility (name, address, content, contact, facility_type, " +
		"business_hours, has_slope, is_entrance_barrier, has_elevator, point, created_at, updated_at) " +
		"VALUES (:name, :address, :content, :contact, :facilityType, " +
		":businessHours, :hasSlope, :isEntranceBarrier, :hasElevator, " +
		"ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), now(), now()) " +
		"RETURNING id", nativeQuery = true)
	Long saveFacilityAndReturnId(@Param("name") String name,
		@Param("address") String address,
		@Param("content") String content,
		@Param("contact") String contact,
		@Param("facilityType") String facilityType,
		@Param("businessHours") String businessHours,
		@Param("hasSlope") Boolean hasSlope,
		@Param("isEntranceBarrier") Boolean isEntranceBarrier,
		@Param("hasElevator") Boolean hasElevator,
		@Param("longitude") BigDecimal longitude,
		@Param("latitude") BigDecimal latitude);
}
