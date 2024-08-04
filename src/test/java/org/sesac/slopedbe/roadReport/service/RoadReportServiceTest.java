package org.sesac.slopedbe.roadReport.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportFormDTO;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCallTaxi;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;
import org.sesac.slopedbe.roadreport.repository.RoadReportCallTaxiRepository;
import org.sesac.slopedbe.roadreport.repository.RoadReportCenterRepository;
import org.sesac.slopedbe.roadreport.repository.RoadReportRepository;
import org.sesac.slopedbe.roadreport.service.RoadReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class RoadReportServiceTest {

	@Autowired
	private RoadReportRepository roadReportRepository;

	@Autowired
	private RoadReportService roadReportService;

	@Autowired
	private RoadReportCenterRepository roadReportCenterRepository;

	@Autowired
	private RoadReportCallTaxiRepository roadReportCallTaxiRepository;

	@Test
	void testAddRoadReport() throws IOException {
		// Given
		RoadReportFormDTO formDTO = RoadReportFormDTO.builder()
			.content("도로에 물이 고여있어 통행이 불편합니다.")
			.latitude(new BigDecimal("37.5665"))
			.longitude(new BigDecimal("126.9780"))
			.address("서울특별시 중구 세종대로 110")
			.build();
		// When
		RoadReport result = roadReportService.addRoadReport(formDTO);

		// Then
		assertNotNull(result);
		assertEquals("도로에 물이 고여있어 통행이 불편합니다.", result.getContent());
		assertEquals("서울특별시 중구 세종대로 110", result.getRoad().getAddress());

		// Cleanup
		roadReportRepository.delete(result);
	}

	@Test
	void testFindClosestCenter() {
		// Given
		BigDecimal latitude = new BigDecimal("37.2636");
		BigDecimal longitude = new BigDecimal("127.0286");
		String mappingCity = "서울";

		// When
		Optional<RoadReportCenter> result = roadReportCenterRepository.findClosestCenter(latitude, longitude, mappingCity);

		// Then
		assertTrue(result.isPresent());
		RoadReportCenter center = result.get();
		assertNotNull(center);
	}

	@Test
	void testFindClosestCallTaxi() {
		// Given
		BigDecimal latitude = new BigDecimal("35.8570");
		BigDecimal longitude = new BigDecimal("128.5183");
		String cityName = "대구광역시";

		// When
		Optional<RoadReportCallTaxi> result = roadReportCallTaxiRepository.findClosestCallTaxi(latitude, longitude, cityName);

		// Then
		assertTrue(result.isPresent());
		RoadReportCallTaxi callTaxi = result.get();
		assertNotNull(callTaxi);
	}

}