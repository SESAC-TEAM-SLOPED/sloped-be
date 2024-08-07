package org.sesac.slopedbe.road.repository;

import org.sesac.slopedbe.road.model.entity.RoadKoreaCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoadKoreaCityRepository extends JpaRepository<RoadKoreaCity, Long> {
	@Query(value = "SELECT r.complaint_region FROM road_korea_city r WHERE r.city_name = :cityName LIMIT 1", nativeQuery = true)	String findComplaintRegionByCityName(String cityName);

	@Query("SELECT r FROM RoadKoreaCity r WHERE r.regionName = :cityName")
	RoadKoreaCity findByRegionName(@Param("cityName") String cityName);

	@Query(value = "SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
		"FROM road_korea_city r " +
		"WHERE r.city_name = :cityName " +
		"AND r.region_name = :regionName " +
		"AND r.complaint_region = :complaintRegion", nativeQuery = true)
	boolean existsByCityNameAndRegionNameAndComplaintRegion(
		@Param("cityName") String cityName,
		@Param("regionName") String regionName,
		@Param("complaintRegion") String complaintRegion);
}
