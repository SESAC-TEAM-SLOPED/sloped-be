package org.sesac.slopedbe.roadreport.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Point;
import org.sesac.slopedbe.common.type.ReportStatus;
import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.road.repository.RoadRepository;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportFormDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportImageDTO;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportImage;
import org.sesac.slopedbe.roadreport.repository.RoadReportImageRepository;
import org.sesac.slopedbe.roadreport.repository.RoadReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoadReportServiceImpl implements RoadReportService {
	private final RoadRepository roadRepository;
	private final RoadReportRepository roadReportRepository;
	private final RoadReportImageRepository roadReportImageRepository;

	@Transactional
	public Road createAndSaveRoad(BigDecimal latitude, BigDecimal longitude, String address) {
		Road road = Road.createRoad(latitude, longitude, address);
		return roadRepository.save(road);
	}

	@Transactional
	public RoadReport addRoadReport(RoadReportFormDTO request) {
		Road road = createAndSaveRoad(request.getLatitude(), request.getLongitude(), request.getAddress());

		RoadReport roadReport = RoadReport.builder()
			.content(request.getContent())
			.status(ReportStatus.PENDING)
			.road(road)
			.build();

		return roadReportRepository.save(roadReport);
	}

	@Transactional
	public RoadReportImage createRoadReportImage(RoadReportImageDTO roadReportImageDTO){

		RoadReport roadReport = roadReportRepository.findById(roadReportImageDTO.getRoadReportId())
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 roadReportId입니다."));

		RoadReportImage roadReportImage = RoadReportImage.builder()
			.url(roadReportImageDTO.getUrl())
			.fileName(roadReportImageDTO.getFileName())
			.uploadOrder(roadReportImageDTO.getUploadOrder())
			.roadReport(roadReport)
			.build();

		return roadReportImageRepository.save(roadReportImage);
	}

	@Override
	public List<Point> getApprovedRoadPoints() {
		List<RoadReport> approvedReports = roadReportRepository.findByStatus(ReportStatus.APPROVED);
		return approvedReports.stream()
			.map(report -> report.getRoad().getPoint())
			.collect(Collectors.toList());
	}

}
