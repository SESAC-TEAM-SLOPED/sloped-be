package org.sesac.slopedbe.roadreport.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.sesac.slopedbe.roadreport.exception.RoadReportErrorCode;
import org.sesac.slopedbe.roadreport.exception.RoadReportException;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCallTaxi;
import org.sesac.slopedbe.roadreport.repository.RoadReportCallTaxiRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoadReportCallTaxiServiceImpl implements RoadReportCallTaxiService {
	private final RoadReportCallTaxiRepository roadReportCallTaxiRepository;

	public Optional<RoadReportCallTaxi> findClosestCallTaxi(BigDecimal latitude, BigDecimal longitude, String cityName) {
		log.info("가장 가까운 콜택시 요청 - 위도: {}, 경도: {}, 도시: {}", latitude, longitude, cityName);
		Optional<RoadReportCallTaxi> closestCallTaxi = roadReportCallTaxiRepository.findClosestCallTaxi(latitude, longitude, cityName);

		if (closestCallTaxi.isPresent()) {
			return closestCallTaxi;
		} else {
			log.error("가장 가까운 콜택시를 찾을 수 없습니다 - 위도: {}, 경도: {}, 도시: {}", latitude, longitude, cityName);
			throw new RoadReportException(RoadReportErrorCode.CLOSEST_CALL_TAXI_NOT_FOUND); // 적절한 오류 코드를 사용하세요.
		}
	}
}
