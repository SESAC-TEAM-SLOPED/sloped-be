package org.sesac.slopedbe.roadreport.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sesac.slopedbe.common.type.ReportStatus;
import org.sesac.slopedbe.gpt.service.GPTService;
import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.road.repository.RoadRepository;
import org.sesac.slopedbe.roadreport.exception.RoadReportErrorCode;
import org.sesac.slopedbe.roadreport.exception.RoadReportException;
import org.sesac.slopedbe.roadreport.model.dto.ReportModalInfoDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportCallTaxiDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportCenterDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportFormDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportImageDTO;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCallTaxi;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportImage;
import org.sesac.slopedbe.roadreport.repository.RoadReportCallTaxiRepository;
import org.sesac.slopedbe.roadreport.repository.RoadReportCenterRepository;
import org.sesac.slopedbe.roadreport.repository.RoadReportImageRepository;
import org.sesac.slopedbe.roadreport.repository.RoadReportRepository;
import org.sesac.slopedbe.roadreport.s3.S3UploadImages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class RoadReportServiceImpl implements RoadReportService {
	private final RoadRepository roadRepository;
	private final RoadReportRepository roadReportRepository;
	private final RoadReportImageRepository roadReportImageRepository;
	private final S3UploadImages s3UploadImages;
	private final RoadReportCenterRepository roadReportCenterRepository;
	private final RoadReportCallTaxiRepository roadReportCallTaxiRepository;
	private final MemberRepository memberRepository;
	private final GPTService gptService;

	@Value("${roadReportDir}")
	private String roadReportDir;

	// 1. Road 저장
	@Transactional
	public Road createAndSaveRoad(BigDecimal latitude, BigDecimal longitude, String address) {
		Road road = Road.createRoad(latitude, longitude, address);
		return roadRepository.save(road);
	}

	// 2. RoadReport 저장
	@Transactional
	public RoadReport addRoadReport(String email, MemberOauthType oauthType, RoadReportFormDTO request) throws IOException {
		log.info("통행 불편 제보 업로드 시작");
		try {
			Road road = createAndSaveRoad(request.getLatitude(), request.getLongitude(), request.getAddress());

			RoadReport newRoadReport = saveRoadReport(email, oauthType, request, road);
			List<String> images = saveRoadReportImages(request.getFiles(), newRoadReport);

			StringBuilder answers = new StringBuilder();

			for (int i = 0; i < images.size(); i++) {
				String answer = gptService.sendImageWithMessage(images.get(i), "이 사진에 나온 길이 교통약자가 이용하기 적합한 공간인지 설명해줘");
				answers.append(i).append(". ").append(answer);
				log.info("[통행불편 제보] 이미지 url: {}", images.get(i));
				log.info("[통행불편 제보] 이미지 캡셔닝 결과: {}", answer);
			}

			String message = answers + "\n 이 설명들을 한줄로 요약해줘";
			String imageCaption = gptService.sendMessage(message);

			addImageCaption(imageCaption, newRoadReport);

			log.info("통행 불편 제보가 성공적으로 제출되었습니다.");
			return newRoadReport;
		} catch (IllegalArgumentException e) {
			log.error("[통행불편 제보] 잘못된 요청: {}", e.getMessage());
			throw new RoadReportException(RoadReportErrorCode.REPORT_IMAGE_UPLOAD_FAILED);
		} catch (Exception e) {
			log.error("[통행제보] 서버 내부 오류 발생", e);
			throw new RoadReportException(RoadReportErrorCode.GENERAL_ERROR);
		}
	}

	private RoadReport saveRoadReport(String email, MemberOauthType oauthType, RoadReportFormDTO request, Road road) {
		Member member = memberRepository.findById(new MemberCompositeKey(email, oauthType))
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

		RoadReport newRoadReport = RoadReport.builder()
			.content(request.getContent())
			.status(ReportStatus.PENDING)
			.road(road)
			.member(member)
			.build();
		return roadReportRepository.save(newRoadReport);
	}

	private void addImageCaption(String imageCaption,RoadReport roadReport) {
		roadReport.addImageCaption(imageCaption);
	}

	// 3. RoadReportImage 저장
	@Transactional
	public List<String> saveRoadReportImages(List<MultipartFile> files, RoadReport newRoadReport) throws IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		List<String> images = new ArrayList<>();

		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			if (!file.getContentType().startsWith("image")) {
				throw new RoadReportException(RoadReportErrorCode.REPORT_IMAGE_UPLOAD_FAILED);
			}

			String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename()); // 확장자명
			String originalFileName = file.getOriginalFilename();
			String baseFileName = originalFileName;

			// 확장자 제거된 파일명
			if (fileExtension != null && !fileExtension.isEmpty()) {
				baseFileName = originalFileName.substring(0, originalFileName.length() - fileExtension.length() - 1);
			}
			String saveFileName =  baseFileName;

			// db에 중복 파일명 찾고, 있는 개수만큼 s3에는 파일명에 넘버링하는 로직
			List<RoadReportImage> existingFiles = roadReportImageRepository.findByFileName(originalFileName);
			if (!existingFiles.isEmpty()) {
				int duplicateCount = existingFiles.size();
				saveFileName = baseFileName + "_" + duplicateCount + "." + fileExtension;
			} else {
				saveFileName = originalFileName;
			}
				log.info("[도로 제보] 파일명: {}", saveFileName);

			String fileUrl = s3UploadImages.upload(file, roadReportDir, saveFileName);

			RoadReportImage roadReportImage = RoadReportImage.builder()
				.url(fileUrl)
				.fileName(file.getOriginalFilename())
				.roadReport(newRoadReport)
				.uploadOrder(i)
				.build();
			roadReportImageRepository.save(roadReportImage);

			images.add(fileUrl);
		}

		return images;
	}

	@Override
	public Optional<RoadReportCenterDTO> findClosestCenter(BigDecimal latitude, BigDecimal longitude, String cityName) {
		log.info("가장 가까운 민원기관 요청 - 위도: {}, 경도: {}, 도시: {}", latitude, longitude, cityName);
		Optional<RoadReportCenter> reportCenter = roadReportCenterRepository.findClosestCenter(latitude, longitude, cityName);
		return reportCenter.map(center -> RoadReportCenterDTO.builder()
			.id(center.getId())
			.centerName(center.getCenterName())
			.centerContact(center.getCenterContact())
			.build());
	}

	@Override
	public Optional<RoadReportCallTaxiDTO> findClosestCallTaxi(BigDecimal latitude, BigDecimal longitude,
		String cityName) {
		log.info("가장 가까운 콜택시 요청 - 위도: {}, 경도: {}, 도시: {}", latitude, longitude, cityName);
		Optional<RoadReportCallTaxi> reportCallTaxi = roadReportCallTaxiRepository.findClosestCallTaxi(latitude, longitude, cityName);
		return reportCallTaxi.map(callTaxi -> RoadReportCallTaxiDTO.builder()
			.callTaxiName(callTaxi.getCallTaxiName())
			.callTaxiContact(callTaxi.getCallTaxiContact())
			.homePage(callTaxi.getHomePage())
			.canOnlineReserve(callTaxi.isCanOnlineReserve())
			.build());
	}

	@Transactional
	public Optional<ReportModalInfoDTO> getReportInfo(Long roadId) {
		log.info("통행제보 전체 조회 - roadId: {}", roadId);

		Optional<RoadReport> roadReportOptional = roadReportRepository.findByRoadId(roadId);
		if (roadReportOptional.isPresent()) {
			RoadReport roadReport = roadReportOptional.get();
			List<RoadReportImage> reportImages = roadReportImageRepository.findByRoadReportId(roadReport.getId())
				.orElseThrow(() -> {
					log.error("해당 마커 이미지를 찾을 수 없습니다 - roadReportId: {}", roadReport.getId());
					return new RoadReportException(RoadReportErrorCode.REPORT_NOT_FOUND);
				});

			List<RoadReportImageDTO> reportImageDTOs = reportImages.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
			log.info("통행불편 제보 정보와 이미지를 성공적으로 불러왔습니다 - roadReportId: {}", roadReport.getId());

			ReportModalInfoDTO reportModalInfoDTO = ReportModalInfoDTO.builder()
				.id(roadReport.getId())
				.reportImageList(reportImageDTOs)
				.content(roadReport.getContent())
				.imageCaption(roadReport.getImageCaption())
				.build();

			return Optional.of(reportModalInfoDTO);
		} else {
			return Optional.empty();
		}
	}

	private RoadReportImageDTO convertToDTO(RoadReportImage roadReportImage) {
		return RoadReportImageDTO.builder()
			.url(roadReportImage.getUrl())
			.fileName(roadReportImage.getFileName())
			.uploadOrder(roadReportImage.getUploadOrder())
			.build();
	}


}
