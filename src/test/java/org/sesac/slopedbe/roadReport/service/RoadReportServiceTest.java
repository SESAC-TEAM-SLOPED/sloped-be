package org.sesac.slopedbe.roadReport.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.repository.MemberRepository;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class RoadReportServiceTest {
	@Autowired
	private RoadReportRepository roadReportRepository;
	@Autowired
	private RoadReportService roadReportService;
	@MockBean
	private ClientRegistrationRepository clientRegistrationRepository;

	@Autowired
	private RoadReportCenterRepository roadReportCenterRepository;

	@Autowired
	private RoadReportCallTaxiRepository roadReportCallTaxiRepository;

	@Autowired
	private MemberRepository memberRepository;


	@Test
	void testAddRoadReport() throws IOException {
		//Assertions.assertThat(1+1).isEqualTo(2);
		// Given
		String email = "kitty7579@gmail.com";
		String nickname = "jane";
		MemberOauthType oauthType = MemberOauthType.LOCAL;
		String memberId = "testId";
		String password = "testpassword";
		Boolean isDisabled = false;

		// Member 생성 및 저장
		Member member = new Member(memberId, password, email, nickname, isDisabled, oauthType);
		memberRepository.save(member);

		// MockMultipartFile 생성
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.png", "image/png", "test image content".getBytes());
		List<MultipartFile> files = Collections.singletonList(mockFile);

		RoadReportFormDTO formDTO = RoadReportFormDTO.builder()
			.content("도로에 물이 고여있어 통행이 불편합니다.")
			.latitude(new BigDecimal("37.5665"))
			.longitude(new BigDecimal("126.9780"))
			.address("서울특별시 중구 세종대로 110")
			.files(files)
			.build();

		// When
		RoadReport result = roadReportService.addRoadReport(email, oauthType, formDTO);
		System.out.println(result.toString());
		// Then
		assertNotNull(result);
		assertEquals("도로에 물이 고여있어 통행이 불편합니다.", result.getContent());
		assertEquals("서울특별시 중구 세종대로 110", result.getRoad().getAddress());
		//assertFalse(result.getRoadReportImages().isEmpty());
		//assertEquals("test.png", result.getRoadReportImages().get(0).getFileName());

		// Cleanup
		roadReportRepository.delete(result);
		memberRepository.delete(member);
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