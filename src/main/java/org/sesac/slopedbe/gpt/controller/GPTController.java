package org.sesac.slopedbe.gpt.controller;

import org.sesac.slopedbe.gpt.service.GPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/gpt")
public class GPTController {

	private final GPTService gptService;

	@PostMapping("/")
	public ResponseEntity<?> getAssistantMsg(@RequestParam String msg) throws JsonProcessingException {
		return gptService.getAssistantMsg(msg);
	}
}
