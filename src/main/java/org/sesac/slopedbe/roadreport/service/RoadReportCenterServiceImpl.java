package org.sesac.slopedbe.roadreport.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sesac.slopedbe.roadreport.model.dto.RoadReportCenterDTO;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;
import org.sesac.slopedbe.roadreport.repository.RoadReportCenterRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoadReportCenterServiceImpl implements RoadReportCenterService{
	private final RoadReportCenterRepository roadReportCenterRepository;

	public Optional<RoadReportCenter> findByRoadId(Long roadId) {
		return roadReportCenterRepository.findByRoadId(roadId);
	}

	@Override
	public List<RoadReportCenterDTO> getComplaintCenterList() {
		try {
			List<RoadReportCenter> reportCenters = roadReportCenterRepository.findAll();

			List<RoadReportCenterDTO> centerListDTO = reportCenters.stream()
				.map(center -> RoadReportCenterDTO.builder()
					.id(center.getId())
					.centerName(center.getCenterName())
					.centerContact(center.getCenterContact())
					.build())
				.collect(Collectors.toList());

			return centerListDTO;
		} catch (Exception e) {
			log.error("민원기관 리스트를 불러오는 중 오류가 발생했습니다.", e);
			throw new RuntimeException("민원기관 리스트를 불러오는 중 오류가 발생했습니다.", e);
		}
	}


	public Optional<RoadReportCenter> findClosestCenter(Double latitude, Double longitude, String mappingCity) {
		return roadReportCenterRepository.findClosestCenter(latitude, longitude, mappingCity);
	}
}
