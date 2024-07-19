package org.sesac.slopedbe.roadreport.service;

import java.util.List;
import java.util.Optional;

import org.sesac.slopedbe.roadreport.model.dto.RoadReportCenterDTO;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;

public interface RoadReportCenterService {
	List<RoadReportCenterDTO> getComplaintCenterList();
	Optional<RoadReportCenter> findClosestCenter(Double longitude, Double latitude, String mappingCity);
}
