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

@RequiredArgsConstructor
@Service
public class RoadReportCsvDataLoader {

	//로그 설정 완료하면 다시 주석 해제하겠습니다 private static final Logger LOGGER = Logger.getLogger(RoadReportCsvDataLoader.class.getName());

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
						throw new IllegalArgumentException("도시명을 찾을 수 없습니다: " + csvRecord.values()[0]);
					}

					RoadReportCenter roadReportCenter = RoadReportCenter.builder()
						.centerName(csvRecord.values()[1])
						.centerContact(csvRecord.values()[2])
						.road(road)
						.city(koreaCity)
						.build();

					roadReportCenterRepository.save(roadReportCenter);

					// LOGGER.info("DB에 저장된 민원기관 레코드: " + roadReportCenter.getCenterName());
				} catch (Exception e) {
					// LOGGER.log(Level.SEVERE, "해당 데이터 레코드 에러: " + csvRecord, e);
					e.printStackTrace(); // 에러 내용을 콘솔에 출력
				}
			}
		} catch (IOException e) {
			// LOGGER.log(Level.SEVERE, "CSV 파일을 읽는 중 예외 발생", e);
			e.printStackTrace(); // 에러 내용을 콘솔에 출력
		}

		// LOGGER.info("CSV 데이터 업로드 완료");
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


	public void saveCitiesFromCsv() {
		try (
			Reader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("korea_citylist.csv").getInputStream(), "UTF-8"));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())
		){
			List<RoadKoreaCity> cities = new ArrayList<>();

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
			}

			cityRepository.saveAll(cities);

			// System.out.println("CSV 파일에서 읽어온 데이터를 데이터베이스에 저장했습니다.");
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}
