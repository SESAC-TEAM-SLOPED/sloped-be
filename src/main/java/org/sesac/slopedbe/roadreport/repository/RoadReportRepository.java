package org.sesac.slopedbe.roadreport.repository;

import java.util.List;

import org.sesac.slopedbe.common.type.ReportStatus;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.springframework.data.repository.CrudRepository;

public interface RoadReportRepository extends CrudRepository<RoadReport, Long> {
	List<RoadReport> findByStatus(ReportStatus status);

}
