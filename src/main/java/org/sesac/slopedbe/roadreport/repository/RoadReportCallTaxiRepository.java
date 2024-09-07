package org.sesac.slopedbe.roadreport.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.sesac.slopedbe.roadreport.model.entity.RoadReportCallTaxi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoadReportCallTaxiRepository extends JpaRepository<RoadReportCallTaxi, Long> {

	@Query(value = "SELECT rct.*, " +
		"ST_Distance(ST_SetSRID(ST_MakePoint(CAST(:longitude AS DOUBLE PRECISION), CAST(:latitude AS DOUBLE PRECISION)), 4326)::geography, r.point::geography) / 1000 AS distance_km " +
		"FROM road_report_call_taxi rct " +
		"JOIN road r ON rct.road_id = r.id " +
		"WHERE r.address LIKE CONCAT(:cityName, '%') " +
		"ORDER BY distance_km " +
		"LIMIT 1", nativeQuery = true)
	Optional<RoadReportCallTaxi> findClosestCallTaxi(@Param("latitude") BigDecimal latitude, @Param("longitude") BigDecimal longitude, @Param("cityName") String cityName);


}
