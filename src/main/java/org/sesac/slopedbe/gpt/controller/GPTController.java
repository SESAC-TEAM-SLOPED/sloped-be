package org.sesac.slopedbe.gpt.controller;

import java.io.IOException;

import org.sesac.slopedbe.gpt.model.dto.GPTRequestDTO;
import org.sesac.slopedbe.gpt.service.GPTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/gpt")
public class GPTController {

	private final GPTService gptService;

	@PostMapping("")
	public ResponseEntity<JsonNode> uploadImageWithMessage(
		@RequestBody GPTRequestDTO gptRequestDTO) throws IOException {
		JsonNode response = gptService.sendImageWithMessage(gptRequestDTO.getImage(), gptRequestDTO.getMessage());

		return ResponseEntity.ok(response);
	}
}
