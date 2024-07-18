package org.sesac.slopedbe.road.repository;

import org.sesac.slopedbe.road.model.entity.RoadKoreaCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoadKoreaCityRepository extends JpaRepository<RoadKoreaCity, Long> {

	@Query("SELECT r.complaintRegion FROM RoadKoreaCity r WHERE r.cityName = :cityName")
	String findComplaintRegionByCityName(String cityName);

	@Query("SELECT r FROM RoadKoreaCity r WHERE r.regionName = :cityName")
	RoadKoreaCity findByRegionName(String cityName);
}
