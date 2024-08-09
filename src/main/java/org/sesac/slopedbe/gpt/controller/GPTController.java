package org.sesac.slopedbe.gpt.controller;

import java.io.IOException;

import org.sesac.slopedbe.gpt.model.dto.GPTRequestDTO;
import org.sesac.slopedbe.gpt.service.GPTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/gpt")
public class GPTController {

	private final GPTService gptService;

	@PostMapping("/image")
	public ResponseEntity<String> uploadImageWithMessage(
		@RequestBody GPTRequestDTO gptRequestDTO) throws IOException {
		String response = "";

		return ResponseEntity.ok(response);
	}

	@PostMapping("")
	public ResponseEntity<String> sendMessage(@RequestBody String message) throws IOException {
		String response = "";

		return ResponseEntity.ok(response);
	}
}
