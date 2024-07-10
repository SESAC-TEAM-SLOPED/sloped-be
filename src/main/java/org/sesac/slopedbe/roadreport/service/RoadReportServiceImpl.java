package org.sesac.slopedbe.roadreport.service;

import java.math.BigDecimal;

import org.sesac.slopedbe.common.type.ReportStatus;
import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.road.repository.RoadRepository;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportForm;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.sesac.slopedbe.roadreport.repository.RoadReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoadReportServiceImpl implements RoadReportService {
	private final RoadRepository roadRepository;
	private final RoadReportRepository roadReportRepository;

	private Road createAndSaveRoad(BigDecimal latitude, BigDecimal longitude, String address) {
		Road road = Road.createRoad(latitude, longitude, address);
		return roadRepository.save(road);
	}

	@Transactional
	public RoadReport addRoadReport(RoadReportForm request) {
		Road road = createAndSaveRoad(request.getLatitude(), request.getLongitude(), request.getAddress());

		RoadReport roadReport = RoadReport.builder()
			.content(request.getContent())
			.status(ReportStatus.PENDING)
			.road(road)
			.build();

		return roadReportRepository.save(roadReport);
	}
}
