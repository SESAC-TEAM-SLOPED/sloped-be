package org.sesac.slopedbe.roadreport.service;

import java.math.BigDecimal;

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
		try {
			Road road = Road.createRoad(latitude, longitude, address);
			return roadRepository.save(road);
		} catch (Exception e) {
			throw new RuntimeException("도로 생성 및 저장 중 오류가 발생했습니다.", e);
		}
	}


	@Transactional
	public RoadReport addRoadReport(RoadReportFormDTO request) {
		try {
			Road road = createAndSaveRoad(request.getLatitude(), request.getLongitude(), request.getAddress());

			RoadReport roadReport = RoadReport.builder()
				.content(request.getContent())
				.status(ReportStatus.PENDING)
				.road(road)
				.build();

			return roadReportRepository.save(roadReport);
		} catch (Exception e) {
			throw new RuntimeException("도로 보고서 생성 중 오류가 발생했습니다.", e);
		}
	}

	@Transactional
	public RoadReportImage createRoadReportImage(RoadReportImageDTO roadReportImageDTO) {
		try {
			RoadReport roadReport = roadReportRepository.findById(roadReportImageDTO.getRoadReportId())
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 roadReport Id입니다."));

			// RoadReportImage 객체 저장
			RoadReportImage roadReportImage = RoadReportImage.builder()
				.url(roadReportImageDTO.getUrl())
				.fileName(roadReportImageDTO.getFileName())
				.uploadOrder(roadReportImageDTO.getUploadOrder())
				.roadReport(roadReport)
				.build();
			return roadReportImageRepository.save(roadReportImage);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("유효하지 않은 roadReportId입니다: " + roadReportImageDTO.getRoadReportId(), e);
		} catch (Exception e) {
			throw new RuntimeException("RoadReportImage 생성 및 저장 중 오류가 발생했습니다.", e);
		}
	}



}
