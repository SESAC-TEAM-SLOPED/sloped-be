package org.sesac.slopedbe.roadreport.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.sesac.slopedbe.roadreport.model.entity.RoadReportCallTaxi;

public interface RoadReportCallTaxiService {

	Optional<RoadReportCallTaxi> findClosestCallTaxi(BigDecimal latitude, BigDecimal longitude, String cityName);
}
