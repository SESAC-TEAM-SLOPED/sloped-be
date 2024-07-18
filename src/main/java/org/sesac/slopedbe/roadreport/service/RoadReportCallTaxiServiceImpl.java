package org.sesac.slopedbe.roadreport.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.sesac.slopedbe.roadreport.model.entity.RoadReportCallTaxi;
import org.sesac.slopedbe.roadreport.repository.RoadReportCallTaxiRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoadReportCallTaxiServiceImpl implements RoadReportCallTaxiService{
	private final RoadReportCallTaxiRepository roadReportCallTaxiRepository;
	public Optional<RoadReportCallTaxi> findClosestCallTaxi(BigDecimal latitude, BigDecimal longitude, String cityName) {
		return roadReportCallTaxiRepository.findClosestCallTaxi(latitude, longitude, cityName);
	}
}
