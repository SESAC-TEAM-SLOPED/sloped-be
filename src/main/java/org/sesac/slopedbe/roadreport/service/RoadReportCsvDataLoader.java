package org.sesac.slopedbe.roadreport.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.road.model.entity.RoadKoreaCity;
import org.sesac.slopedbe.road.repository.RoadKoreaCityRepository;
import org.sesac.slopedbe.road.repository.RoadRepository;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCallTaxi;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;
import org.sesac.slopedbe.roadreport.repository.RoadReportCallTaxiRepository;
import org.sesac.slopedbe.roadreport.repository.RoadReportCenterRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class RoadReportCsvDataLoader {

	private final RoadRepository roadRepository;
	private final RoadReportCenterRepository roadReportCenterRepository;
	private final RoadReportCallTaxiRepository roadReportCallTaxiRepository;
	private final RoadKoreaCityRepository cityRepository;
	@PostConstruct
	public void loadCsvData() {
		saveCitiesFromCsv();
		loadCenterCsvData();
		loadTaxiCsvData();
	}


	public void loadCenterCsvData() {
		log.info("민원기관 CSV 파일 로딩 시작");
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

					RoadKoreaCity koreaCity = cityRepository.findByRegionName(csvRecord.values()[0]);
					if (koreaCity == null) {
						throw new IllegalArgumentException("시/도명을 찾을 수 없습니다: " + csvRecord.values()[0]);
					}

					RoadReportCenter roadReportCenter = RoadReportCenter.builder()
						.centerName(csvRecord.values()[1])
						.centerContact(csvRecord.values()[2])
						.road(road)
						.city(koreaCity)
						.build();

					roadReportCenterRepository.save(roadReportCenter);

					log.info("DB에 저장된 민원기관 레코드: " + roadReportCenter.getCenterName());
				} catch (Exception e) {
					log.error("해당 민원기관 데이터 레코드 에러: " + csvRecord, e);
				}
			}
		} catch (IOException e) {
			log.error("민원기관 CSV 파일을 읽는 중 예외 발생", e);
		}

		log.info("민원기관 CSV 데이터 업로드 완료");
	}


	public void loadTaxiCsvData() {
		log.info("콜택시 CSV 파일 로딩 시작");

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

					log.info("DB에 저장된 콜택시 레코드: " + roadReportCallTaxi.getCallTaxiName());
				} catch (Exception e) {
					log.error("해당 콜택시 데이터 레코드 에러: " + csvRecord, e);
				}
			}
		} catch (IOException e) {
			log.error("콜택시 CSV 파일을 읽는 중 예외 발생", e);
		}

		log.info("콜택시 CSV 데이터 업로드 완료");
	}


	public void saveCitiesFromCsv() {
		log.info("지역 정보 CSV 파일 로딩 시작");

		try (
			Reader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("korea_citylist.csv").getInputStream(), "UTF-8"));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())
		){
			List<RoadKoreaCity> cities = new ArrayList<>();
			int recordCount = 0;

			for (CSVRecord csvRecord : csvParser) {
				String cityName = csvRecord.values()[0];
				String regionName = csvRecord.values()[1];
				String complaintRegion = csvRecord.values()[2];

				RoadKoreaCity city = RoadKoreaCity.builder()
					.cityName(cityName)
					.regionName(regionName)
					.complaintRegion(complaintRegion)
					.build();

				cities.add(city);
				recordCount++;
				log.debug("CSV 레코드 처리: {}, {}, {}", cityName, regionName, complaintRegion);

			}

			cityRepository.saveAll(cities);
			log.info("총 {}개의 지역 정보가 데이터베이스에 저장되었습니다.", recordCount);


		} catch(IOException e){
			log.error("전국 시/도 지역정보 CSV 파일을 읽는 중 예외 발생", e);
		}
	}
}
