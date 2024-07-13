package org.sesac.slopedbe.roadreport.service;

import java.math.BigDecimal;
import java.util.List;

import org.locationtech.jts.geom.Point;
import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportFormDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportImageDTO;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportImage;

public interface RoadReportService {
	Road createAndSaveRoad(BigDecimal latitude, BigDecimal longitude, String address);
	RoadReport addRoadReport(RoadReportFormDTO request);
	RoadReportImage createRoadReportImage(RoadReportImageDTO roadReportImageDTO);
	List<Point> getApprovedRoadPoints();
}
