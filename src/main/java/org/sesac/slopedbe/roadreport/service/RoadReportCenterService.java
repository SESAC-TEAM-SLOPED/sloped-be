package org.sesac.slopedbe.roadreport.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;

public interface RoadReportCenterService {

	Optional<RoadReportCenter> findClosestCenter(BigDecimal longitude, BigDecimal latitude, String mappingCity);
}
