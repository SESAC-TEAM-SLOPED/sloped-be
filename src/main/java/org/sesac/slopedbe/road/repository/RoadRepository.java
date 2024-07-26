package org.sesac.slopedbe.road.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.sesac.slopedbe.road.model.entity.Road;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RoadRepository extends CrudRepository<Road, Long> {
	// @Query(value = "SELECT r.* FROM road r " +
	// 	"ORDER BY ST_Distance(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, r.point::geography) / 1000 " +
	// 	"LIMIT 1", nativeQuery = true)
	// Optional<Road> findClosestRoad(BigDecimal latitude, BigDecimal longitude);

}
