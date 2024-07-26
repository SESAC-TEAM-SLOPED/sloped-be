package org.sesac.slopedbe.road.repository;

import org.sesac.slopedbe.road.model.entity.RoadKoreaCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoadKoreaCityRepository extends JpaRepository<RoadKoreaCity, Long> {

	@Query(value = "SELECT r.complaint_region FROM road_korea_city r WHERE r.city_name = :cityName LIMIT 1", nativeQuery = true)	String findComplaintRegionByCityName(String cityName);

	@Query("SELECT r FROM RoadKoreaCity r WHERE r.regionName = :cityName")
	RoadKoreaCity findByRegionName(String cityName);
}
