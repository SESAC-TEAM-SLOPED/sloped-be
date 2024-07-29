package org.sesac.slopedbe.roadreport.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.sesac.slopedbe.roadreport.model.entity.RoadReportCallTaxi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadReportCallTaxiRepository extends JpaRepository<RoadReportCallTaxi, Long> {

	@Query(value = "SELECT rct.*, " +
		"ST_Distance(ST_SetSRID(ST_MakePoint(CAST(:longitude AS DOUBLE PRECISION), CAST(:latitude AS DOUBLE PRECISION)), 4326)::geography, r.point::geography) / 1000 AS distance_km " +
		"FROM road_report_call_taxi rct " +
		"JOIN road r ON rct.road_id = r.id " +
		"WHERE r.address LIKE CONCAT(:cityName, '%') " +
		"ORDER BY distance_km " +
		"LIMIT 1", nativeQuery = true)
	Optional<RoadReportCallTaxi> findClosestCallTaxi(BigDecimal latitude, BigDecimal longitude, String cityName);

	@Query(value = "SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM road_report_call_taxi c " +
		"JOIN road r ON c.road_id = r.id " +
		"WHERE ST_Equals(r.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) " +
		"AND c.call_taxi_name = :callTaxiName AND c.call_taxi_contact = :callTaxiContact", nativeQuery = true)
	boolean existsByLocationAndCallTaxiNameAndCallTaxiContact(@Param("latitude") double latitude,
		@Param("longitude") double longitude,
		@Param("callTaxiName") String callTaxiName,
		@Param("callTaxiContact") String callTaxiContact);


}
