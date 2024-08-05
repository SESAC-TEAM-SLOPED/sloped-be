package org.sesac.slopedbe.road.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Point;
import org.sesac.slopedbe.common.type.ReportStatus;
import org.sesac.slopedbe.road.model.dto.RoadMarkerInfoDTO;
import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.road.repository.RoadRepository;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.sesac.slopedbe.roadreport.repository.RoadReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class RoadServiceImpl implements RoadService {
	private final RoadReportRepository roadReportRepository;
	private final RoadRepository roadRepository;

	@Transactional
	public List<RoadMarkerInfoDTO> getApprovedRoadPoints() {
		try {
			log.info("getApprovedRoadPoints 호출");

			List<RoadReport> approvedReports = roadReportRepository.findByStatus(ReportStatus.APPROVED);

			if (approvedReports.isEmpty()) {
				log.info("승인된 통행 불편 제보가 없습니다.");
				return Collections.emptyList();
			}

			List<RoadMarkerInfoDTO> roadMarkerInfoDTOList = approvedReports.stream()
				.map(report -> {
					Road road = report.getRoad();
					return new RoadMarkerInfoDTO(
						road.getId(),
						road.getLatitude(),
						road.getLongitude(),
						road.getAddress()
					);
				})
				.collect(Collectors.toList());

			log.info("승인된 통행 불편 제보를 성공적으로 불러왔습니다. 총 개수: {}", roadMarkerInfoDTOList.size());
			return roadMarkerInfoDTOList;

		} catch (Exception e) {
			log.error("승인된 통행 불편 제보를 불러오는 중 오류가 발생했습니다.", e);
			throw new RuntimeException("승인된 통행 불편 제보를 불러오는 중 오류가 발생했습니다.", e);
		}
	}

	@Transactional
	public RoadMarkerInfoDTO findByRoaId(Long roadId) {
		Optional<Road> roadOptional = roadRepository.findRoadByRoadId(roadId);
		if (roadOptional.isPresent()) {
			Road road = roadOptional.get();
			Point point = road.getPoint();
			BigDecimal latitude = BigDecimal.valueOf(point.getY());
			BigDecimal longitude = BigDecimal.valueOf(point.getX());

			return RoadMarkerInfoDTO.builder()
				.id(road.getId())
				.latitude(latitude)
				.longitude(longitude)
				.address(road.getAddress())
				.build();
		}
		log.warn("해당 roadId로 도로를 찾을 수 없음 - roadId: {}", roadId);
		return null;
	}
}
