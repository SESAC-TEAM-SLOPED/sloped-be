package org.sesac.slopedbe.gpt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GPTServiceImpl implements GPTService {
	@Value("${openai.secret-key}")
	private String secretKey;
	private final RestTemplate restTemplate;

	public GPTServiceImpl() {
		this.restTemplate = new RestTemplate();
	}

	public JsonNode sendImageWithMessage(String imageUrl, String message) throws JsonProcessingException {
		final String url = "https://api.openai.com/v1/chat/completions";

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

		Map<String,Object> image = new HashMap<>();
		image.put("type", "image_url");
		Map<String,String> image_url = new HashMap<>();
		image_url.put("url", imageUrl);
		image.put("image_url", image_url);
		content.add(image);

		userMessage.put("content", content);
		messages.add(userMessage);

		bodyMap.put("messages", messages);

		String body = mapper.writeValueAsString(bodyMap);

		// Create HttpEntity
		HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

		// Send the request
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		return mapper.readTree(responseEntity.getBody());
	}
}
