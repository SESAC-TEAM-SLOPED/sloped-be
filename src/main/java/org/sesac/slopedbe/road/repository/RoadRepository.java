package org.sesac.slopedbe.road.repository;

import java.util.Optional;

import org.sesac.slopedbe.road.model.entity.Road;
import org.springframework.context.annotation.Description;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoadRepository extends JpaRepository<Road, Long> {


	@Query(value = "SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM road r " +
		"WHERE ST_Equals(r.point, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) AND r.address = :address", nativeQuery = true)
	boolean existsByLatitudeAndLongitudeAndAddress(@Param("latitude") Double latitude,
		@Param("longitude") Double longitude,
		@Param("address") String address);


	@Query("SELECT r FROM Road r WHERE r.id = :roadId")
	Optional<Road> findRoadByRoadId(@Param("roadId") Long roadId);
}
