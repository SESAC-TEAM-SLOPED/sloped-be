package org.sesac.slopedbe.gpt.service;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface GPTService {
	ResponseEntity<?> getAssistantMsg(String userMsg) throws JsonProcessingException;
}
