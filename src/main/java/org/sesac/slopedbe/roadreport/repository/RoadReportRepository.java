package org.sesac.slopedbe.roadreport.repository;

import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.springframework.data.repository.CrudRepository;

public interface RoadReportRepository extends CrudRepository<RoadReport, Long> {
}
