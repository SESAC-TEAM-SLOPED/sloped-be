package org.sesac.slopedbe.roadreport.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadReportCenterRepository extends JpaRepository<RoadReportCenter, Long> {
	@Query("SELECT rrc FROM RoadReportCenter rrc WHERE rrc.road.id = :roadId")
	Optional<RoadReportCenter> findByRoadId(Long roadId);


	@Query(value = "SELECT rrc.*, " +
		"ST_Distance(ST_SetSRID(ST_MakePoint(CAST(:longitude AS DOUBLE PRECISION), CAST(:latitude AS DOUBLE PRECISION)), 4326)::geography, r.point::geography) / 1000 AS distance_km " +
		"FROM road_report_center rrc " +
		"JOIN road r ON rrc.road_id = r.id " +
		"ORDER BY distance_km " +
		"LIMIT 1", nativeQuery = true)
	Optional<RoadReportCenter> findClosestCenter(BigDecimal latitude, BigDecimal longitude);

}
