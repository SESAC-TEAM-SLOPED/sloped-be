package org.sesac.slopedbe.roadreport.service;

import org.sesac.slopedbe.roadreport.model.dto.RoadReportForm;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;

public interface RoadReportService {

	RoadReport addRoadReport(RoadReportForm request);
}
