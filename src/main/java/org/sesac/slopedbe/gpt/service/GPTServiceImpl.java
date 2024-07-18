package org.sesac.slopedbe.gpt.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

	public JsonNode callChatGPT(String userMsg) throws JsonProcessingException {
		final String url = "https://api.openai.com/v1/chat/completions";

		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(secretKey);

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> bodyMap = new HashMap<>();
		bodyMap.put("model", "gpt-4");

		List<Map<String,String>> messages = new ArrayList<>();
		Map<String, String> userMessage = new HashMap<>();
		userMessage.put("role", "user");
		userMessage.put("content", userMsg);
		messages.add(userMessage);

		Map<String, String> assistantMessage = new HashMap<>();
		assistantMessage.put("role", "system");
		assistantMessage.put("content", "너는 친절한 AI야");
		messages.add(assistantMessage);

		bodyMap.put("messages", messages);

		String body = mapper.writeValueAsString(bodyMap);

		HttpEntity<String> request = new HttpEntity<>(body, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

		return mapper.readTree(response.getBody());
	}

	@Override
	public ResponseEntity<?> getAssistantMsg(String userMsg) throws JsonProcessingException {
		JsonNode jsonNode = callChatGPT(userMsg);
		String content = jsonNode.path("choices").get(0).path("message").path("content").asText();

		return ResponseEntity.status(HttpStatus.OK).body(content);
	}
}
