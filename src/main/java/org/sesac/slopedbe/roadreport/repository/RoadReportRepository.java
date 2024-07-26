package org.sesac.slopedbe.roadreport.repository;

import java.util.List;
import java.util.Optional;

import org.sesac.slopedbe.common.type.ReportStatus;
import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RoadReportRepository extends CrudRepository<RoadReport, Long> {
	List<RoadReport> findByStatus(ReportStatus status);
	Optional<RoadReport> findByRoadId(Long roadId);

	@Query("SELECT r FROM Road r WHERE r.id = :roadId")
	Optional<Road> findRoadByRoadId(Long roadId);

}
