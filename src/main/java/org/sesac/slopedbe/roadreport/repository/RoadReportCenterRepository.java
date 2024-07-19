package org.sesac.slopedbe.roadreport.repository;

import java.util.Optional;

import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadReportCenterRepository extends JpaRepository<RoadReportCenter, Long> {
	@Query("SELECT rrc FROM RoadReportCenter rrc WHERE rrc.road.id = :roadId")
	Optional<RoadReportCenter> findByRoadId(Long roadId);


	@Query(value = "SELECT rrc.* " +
		"FROM road_report_center rrc " +
		"JOIN road r ON rrc.road_id = r.id " +
		"JOIN road_korea_city c ON rrc.city_id = c.id " +
		"WHERE c.city_name LIKE CONCAT(:mappingCity, '%') " +
		"ORDER BY ST_Distance(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, r.point::geography) / 1000 ASC " +
		"LIMIT 1", nativeQuery = true)
	Optional<RoadReportCenter> findClosestCenter(Double latitude, Double longitude, String mappingCity);

}
