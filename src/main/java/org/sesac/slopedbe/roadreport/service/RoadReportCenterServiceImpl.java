package org.sesac.slopedbe.roadreport.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sesac.slopedbe.roadreport.exception.RoadReportErrorCode;
import org.sesac.slopedbe.roadreport.exception.RoadReportException;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportCenterDTO;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;
import org.sesac.slopedbe.roadreport.repository.RoadReportCenterRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoadReportCenterServiceImpl implements RoadReportCenterService {
	private final RoadReportCenterRepository roadReportCenterRepository;

	public Optional<RoadReportCenter> findByRoadId(Long roadId) {
		Optional<RoadReportCenter> center = roadReportCenterRepository.findByRoadId(roadId);
		if (center.isEmpty()) {
			throw new RoadReportException(RoadReportErrorCode.ROAD_ID_NOT_FOUND);
		}
		return center;
	}

	@Override
	public List<RoadReportCenterDTO> getComplaintCenterList() {
		try {
			log.info("민원기관 리스트 요청");
			List<RoadReportCenter> reportCenters = roadReportCenterRepository.findAll();

			List<RoadReportCenterDTO> centerListDTO = reportCenters.stream()
				.map(center -> RoadReportCenterDTO.builder()
					.id(center.getId())
					.centerName(center.getCenterName())
					.centerContact(center.getCenterContact())
					.build())
				.collect(Collectors.toList());
			log.info("민원기관 리스트 반환 성공, 총 개수: {}", centerListDTO.size());
			return centerListDTO;
		} catch (Exception e) {
			log.error("민원기관 리스트를 불러오는 중 오류가 발생했습니다.", e);
			throw new RoadReportException(RoadReportErrorCode.CENTER_LIST_FETCH_FAILED);
		}
	}

	public Optional<RoadReportCenter> findClosestCenter(BigDecimal latitude, BigDecimal longitude, String mappingCity) {
		log.info("가장 가까운 민원기관 요청 - 위도: {}, 경도: {}, 도시: {}", latitude, longitude, mappingCity);
		Optional<RoadReportCenter> closestCenter = roadReportCenterRepository.findClosestCenter(latitude, longitude, mappingCity);

		return Optional.ofNullable(closestCenter.orElseThrow(() -> {
			log.error("가장 가까운 민원기관을 찾을 수 없습니다 - 위도: {}, 경도: {}, 도시: {}", latitude, longitude, mappingCity);
			return new RoadReportException(RoadReportErrorCode.CLOSEST_CENTER_NOT_FOUND);
		}));
	}
}
