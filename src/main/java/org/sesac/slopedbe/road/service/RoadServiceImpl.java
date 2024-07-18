package org.sesac.slopedbe.road.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
public class RoadServiceImpl implements RoadService{
	private final RoadReportRepository roadReportRepository;
	private final RoadRepository roadRepository;

	@Transactional
	public List<RoadMarkerInfoDTO> getApprovedRoadPoints() {
		try {
			log.info("getApprovedRoadPoints 호출");

			List<RoadReport> approvedReports = roadReportRepository.findByStatus(ReportStatus.APPROVED);

			if (approvedReports.isEmpty()) {
				log.warn("승인된 통행 불편 제보가 없습니다.");
				return Collections.emptyList();
			}

			// 승인된 보고서를 RoadMarkerInfoDTO로 변환
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
}