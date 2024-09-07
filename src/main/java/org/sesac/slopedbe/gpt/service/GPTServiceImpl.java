package org.sesac.slopedbe.gpt.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sesac.slopedbe.facilityreview.model.entity.FacilityReviewImage;
import org.sesac.slopedbe.facilityreview.repository.FacilityReviewImageRepository;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.sesac.slopedbe.roadreport.repository.RoadReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class GPTServiceImpl implements GPTService {
	private final RestTemplate restTemplate = new RestTemplate();
	private final FacilityReviewImageRepository facilityReviewImageRepository;
	private final RoadReportRepository roadReportRepository;

	@Value("${openai.secret-key}")
	private String secretKey;
	private String url = "https://api.openai.com/v1/chat/completions";

	@Async
	public void generateReviewImageCaption(List<FacilityReviewImage> images) throws IOException {
		for (int i = 0; i < images.size(); i++) {
			String answer = sendImageWithMessage(images.get(i).getUrl(), "이 사진에 나온 길이 교통약자가 이용하기 적합한 공간인지 설명해줘");
			log.info("[리뷰 이미지 캡셔닝] 이미지 url: {}", images.get(i).getUrl());
			log.info("[리뷰 이미지 캡셔닝] 이미지 캡셔닝 결과: {}", answer);

			updateReviewImageWithCaption(images.get(i).getUrl(), answer);
		}
	}

	@Async
	public void generateRoadImageCaption(Long roadReportId,
		List<String> images) throws IOException {

		StringBuilder answers = new StringBuilder();

		for (int i = 0; i < images.size(); i++) {
			long startTime = System.currentTimeMillis(); // 시작 시간

			String answer = sendImageWithMessage(images.get(i), "이 사진에 나온 길이 교통약자가 이용하기 적합한 공간인지 설명해줘");
			answers.append(i).append(". ").append(answer);

			long endTime = System.currentTimeMillis(); // 종료 시간
			long duration = endTime - startTime; // 처리 시간 계산

			log.info("[통행불편 제보] 이미지 url: {}", images.get(i));
			log.info("[통행불편 제보] 이미지 캡셔닝 결과: {}", answer);
			log.info("[통행불편 제보] 이미지당 {} 처리 시간: {} ms", i, duration); // 처리 시간 로그 출력
		}

		String message = answers + "\n 이 설명들을 한줄로 요약해줘";
		String imageCaption = sendMessage(message);

		updateRoadReportWithCaption(roadReportId, imageCaption);
	}

	private String sendMessage(String message) throws JsonProcessingException {
		// 시작 시간 기록
		long startTime = System.currentTimeMillis();

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(secretKey);

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> bodyMap = new HashMap<>();
		bodyMap.put("model", "gpt-4o-mini");

		List<Map<String, Object>> messages = new ArrayList<>();
		Map<String, Object> userMessage = new HashMap<>();
		userMessage.put("role", "user");

		List<Map<String, Object>> content = new ArrayList<>();

		Map<String, Object> text = new HashMap<>();
		text.put("type", "text");
		text.put("text", message);
		content.add(text);

		userMessage.put("content", content);
		messages.add(userMessage);

		bodyMap.put("messages", messages);

		String body = mapper.writeValueAsString(bodyMap);

		// Create HttpEntity
		HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

		// Send the request
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		// 종료 시간 기록
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime; // 처리 시간 계산

		// 처리 시간 로그 출력
		log.info("통행불편 제보 한 줄 요약 총 처리 시간: {} ms", duration);


		return mapper.readTree(responseEntity.getBody()).get("choices").get(0).get("message").get("content").toString();
	}

	private String sendImageWithMessage(String imageUrl, String message) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(secretKey);

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> bodyMap = new HashMap<>();
		bodyMap.put("model", "gpt-4o-mini");

		List<Map<String, Object>> messages = new ArrayList<>();
		Map<String, Object> userMessage = new HashMap<>();
		userMessage.put("role", "user");

		List<Map<String, Object>> content = new ArrayList<>();

		Map<String, Object> text = new HashMap<>();
		text.put("type", "text");
		text.put("text", message);
		content.add(text);

		Map<String, Object> image = new HashMap<>();
		image.put("type", "image_url");
		Map<String, String> image_url = new HashMap<>();
		image_url.put("url", imageUrl);
		image.put("image_url", image_url);
		content.add(image);

		userMessage.put("content", content);
		messages.add(userMessage);

		bodyMap.put("messages", messages);

		String body = mapper.writeValueAsString(bodyMap);

		// Create HttpEntity
		HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
				String.class);

			return mapper.readTree(responseEntity.getBody())
				.get("choices")
				.get(0)
				.get("message")
				.get("content")
				.toString();
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().is4xxClientError()) {
				return "유효하지 않은 이미지";
			}
			throw new HttpClientErrorException(e.getStatusCode(), e.getResponseBodyAsString());
		}
	}

	private void updateReviewImageWithCaption(String imageId, String caption) {
		FacilityReviewImage facilityReviewImage = facilityReviewImageRepository.findById(imageId).orElseThrow(null);

		facilityReviewImage.addImageCaption(caption);
		facilityReviewImageRepository.save(facilityReviewImage);
	}

	private void updateRoadReportWithCaption(Long roadReportId, String caption) {
		RoadReport roadReport = roadReportRepository.findById(roadReportId).orElseThrow();

		roadReport.addImageCaption(caption);
		roadReportRepository.save(roadReport);
	}
}
