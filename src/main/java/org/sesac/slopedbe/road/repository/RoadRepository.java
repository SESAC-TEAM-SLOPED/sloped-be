package org.sesac.slopedbe.road.repository;

import org.sesac.slopedbe.road.model.entity.Road;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RoadRepository extends CrudRepository<Road, Long> {
	// @Query(value = "SELECT r.* FROM road r " +
	// 	"ORDER BY ST_Distance(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, r.point::geography) / 1000 " +
	// 	"LIMIT 1", nativeQuery = true)
	// Optional<Road> findClosestRoad(BigDecimal latitude, BigDecimal longitude);

	@Query(value = "SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM road r " +
		"WHERE ST_Equals(r.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) AND r.address = :address", nativeQuery = true)
	boolean existsByLatitudeAndLongitudeAndAddress(@Param("latitude") Double latitude,
		@Param("longitude") Double longitude,
		@Param("address") String address);

}
