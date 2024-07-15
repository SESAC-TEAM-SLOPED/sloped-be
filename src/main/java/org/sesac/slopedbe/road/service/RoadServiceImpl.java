package org.sesac.slopedbe.road.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sesac.slopedbe.common.type.ReportStatus;
import org.sesac.slopedbe.road.model.dto.RoadMarkerInfoDTO;
import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.road.repository.RoadRepository;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.sesac.slopedbe.roadreport.repository.RoadReportRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoadServiceImpl implements RoadService{
	private final RoadReportRepository roadReportRepository;
	private final RoadRepository roadRepository;

	@Override
	public List<RoadMarkerInfoDTO> getApprovedRoadPoints() {
		List<RoadReport> approvedReports = roadReportRepository.findByStatus(ReportStatus.APPROVED);

		return approvedReports.stream()
			.map(report -> {
				Road road = report.getRoad();
				return new RoadMarkerInfoDTO(
					road.getId(),
					road.getLatitude(),
					road.getLongitude()
				);
			})
			.collect(Collectors.toList());
	}

	public Optional<Road> findClosestRoad(BigDecimal latitude, BigDecimal longitude) {
		return roadRepository.findClosestRoad(latitude, longitude);
	}

}
