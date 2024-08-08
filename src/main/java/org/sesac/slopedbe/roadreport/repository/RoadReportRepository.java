package org.sesac.slopedbe.roadreport.repository;

import java.util.List;
import java.util.Optional;

import org.sesac.slopedbe.common.type.ReportStatus;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface RoadReportRepository extends JpaRepository<RoadReport, Long> {
	List<RoadReport> findByStatus(ReportStatus status);
	Optional<RoadReport> findByRoadId(@Param("roadId") Long roadId);



}
