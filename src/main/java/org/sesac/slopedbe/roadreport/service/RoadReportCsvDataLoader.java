package org.sesac.slopedbe.roadreport.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.road.repository.RoadRepository;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCallTaxi;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;
import org.sesac.slopedbe.roadreport.repository.RoadReportCallTaxiRepository;
import org.sesac.slopedbe.roadreport.repository.RoadReportCenterRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoadReportCsvDataLoader {

	//로그 설정 완료하면 다시 주석 해제하겠습니다 private static final Logger LOGGER = Logger.getLogger(RoadReportCsvDataLoader.class.getName());

	private final RoadRepository roadRepository;
	private final RoadReportCenterRepository roadReportCenterRepository;
	private final RoadReportCallTaxiRepository roadReportCallTaxiRepository;

	@PostConstruct
	public void loadCsvData() {
		loadCenterCsvData();
		loadTaxiCsvData();
	}


	public void loadCenterCsvData() {
		//LOGGER.info("CSV 파일 로딩 시작");

		try (
			Reader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("road_trouble_center_list.csv").getInputStream(), "UTF-8"));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())
		) {
			for (CSVRecord csvRecord : csvParser) {
				try {
					BigDecimal latitude = new BigDecimal(csvRecord.get("위도"));
					BigDecimal longitude = new BigDecimal(csvRecord.get("경도"));
					String address = csvRecord.get("주소");
					Road road = Road.createRoad(latitude, longitude, address);
					roadRepository.save(road);

					RoadReportCenter roadReportCenter = RoadReportCenter.builder()
						.centerName(csvRecord.values()[0])
						.centerContact(csvRecord.values()[1])
						.road(road)
						.build();

					roadReportCenterRepository.save(roadReportCenter);

					//LOGGER.info("DB에 저장된 민원기관 레코드: " + roadReportCenter.getCenterName());
				} catch (Exception e) {
					//LOGGER.log(Level.SEVERE, "해당 데이터 레코드 에러: " + csvRecord, e);
				}
			}
		} catch (IOException e) {
			//LOGGER.log(Level.SEVERE, "CSV 파일을 읽는 중 예외 발생", e);
		}

		//LOGGER.info("CSV 데이터 업로드 완료");
	}


	public void loadTaxiCsvData() {
		//LOGGER.info("CSV 파일 로딩 시작");

		try (
			Reader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("road_callTaxi.csv").getInputStream(), "UTF-8"));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())
		) {
			for (CSVRecord csvRecord : csvParser) {
				String callTaxiName = csvRecord.values()[0];
				String callTaxiContact = csvRecord.values()[4];
				String homePage = csvRecord.values()[5];
				boolean canOnlineReserve = csvRecord.values()[6].equals("o");

				try {
					BigDecimal latitude = new BigDecimal(csvRecord.get("위도"));
					BigDecimal longitude = new BigDecimal(csvRecord.get("경도"));
					String address = csvRecord.get("주소");
					Road road = Road.createRoad(latitude, longitude, address);
					roadRepository.save(road);

					RoadReportCallTaxi roadReportCallTaxi = RoadReportCallTaxi.builder()
						.callTaxiName(callTaxiName)
						.callTaxiContact(callTaxiContact)
						.canOnlineReserve(canOnlineReserve)
						.homePage(homePage)
						.road(road)
						.build();

					roadReportCallTaxiRepository.save(roadReportCallTaxi);

					//LOGGER.info("DB에 저장된 민원기관 레코드: " + roadReportCenter.getCenterName());
				} catch (Exception e) {
					//LOGGER.log(Level.SEVERE, "해당 데이터 레코드 에러: " + csvRecord, e);
				}
			}
		} catch (IOException e) {
			//LOGGER.log(Level.SEVERE, "CSV 파일을 읽는 중 예외 발생", e);
		}

		//LOGGER.info("CSV 데이터 업로드 완료");
	}
}
