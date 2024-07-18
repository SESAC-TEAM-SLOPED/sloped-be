package org.sesac.slopedbe.roadreport.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;
import org.sesac.slopedbe.roadreport.repository.RoadReportCenterRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoadReportCenterServiceImpl implements RoadReportCenterService{
	private final RoadReportCenterRepository roadReportCenterRepository;

	public Optional<RoadReportCenter> findByRoadId(Long roadId) {
		return roadReportCenterRepository.findByRoadId(roadId);
	}

	public Optional<RoadReportCenter> findClosestCenter(BigDecimal latitude, BigDecimal longitude, String mappingCity) {
		return roadReportCenterRepository.findClosestCenter(latitude, longitude, mappingCity);
	}
}
