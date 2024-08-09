package org.sesac.slopedbe.roadreport.repository;

import java.util.List;
import java.util.Optional;

import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoadReportRepository extends JpaRepository<RoadReport, Long> {
	@Query(value = "SELECT rr.* " +
		"FROM road_report rr " +
		"JOIN road r ON rr.road_id = r.id " +
		"WHERE rr.status = :status " +
		"AND ST_DWithin(r.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :distance_meters) " +
		"ORDER BY ST_Distance(r.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) " +
		"LIMIT :limit",
		nativeQuery = true)
	List<RoadReport> findByLocationAndStatus(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("distance_meters") double distance_meters, @Param("limit") int limit, @Param("status") String status);
	Optional<RoadReport> findByRoadId(@Param("roadId") Long roadId);



}
