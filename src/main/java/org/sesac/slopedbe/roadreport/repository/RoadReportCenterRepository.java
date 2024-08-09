package org.sesac.slopedbe.roadreport.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoadReportCenterRepository extends JpaRepository<RoadReportCenter, Long> {
	@Query("SELECT rrc FROM RoadReportCenter rrc WHERE rrc.road.id = :roadId")
	Optional<RoadReportCenter> findByRoadId(@Param("roadId") Long roadId);


	@Query(value = "SELECT rrc.* " +
		"FROM road_report_center rrc " +
		"JOIN road r ON rrc.road_id = r.id " +
		"JOIN road_korea_city c ON rrc.city_id = c.id " +
		"WHERE c.region_name LIKE CONCAT(:mappingCity, '%')" +
		"ORDER BY ST_Distance(ST_SetSRID(ST_MakePoint(CAST(:longitude AS DOUBLE PRECISION), CAST(:latitude AS DOUBLE PRECISION)), 4326)::geography, r.point::geography) / 1000 ASC " +
		"LIMIT 1", nativeQuery = true)
	Optional<RoadReportCenter> findClosestCenter(@Param("latitude") BigDecimal latitude, @Param("longitude") BigDecimal longitude, @Param("mappingCity") String mappingCity);

	@Query(value = "SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM road_report_center c " +
		"JOIN road r ON c.road_id = r.id WHERE ST_DWithin(r.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), 0) " +
		"AND c.center_name = :centerName AND r.address = :address AND c.center_contact = :centerContact", nativeQuery = true)
	boolean existsByLocationAndCenterNameAndCenterContact(@Param("latitude") double latitude,
		@Param("longitude") double longitude,
		@Param("centerName") String centerName,
		@Param("address") String address,
		@Param("centerContact") String centerContact);
}
