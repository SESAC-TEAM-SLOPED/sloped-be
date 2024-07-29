package org.sesac.slopedbe.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.sesac.slopedbe.auth.exception.InvalidVerificationCodeException;
import org.sesac.slopedbe.auth.exception.MemberAlreadyExistsException;
import org.sesac.slopedbe.auth.exception.MemberNotFoundException;
import org.sesac.slopedbe.auth.model.dto.request.MailVerificationRequest;
import org.sesac.slopedbe.auth.service.MailAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class MailController {

	private final MailAuthenticationService mailAuthenticationService;

	@Operation(summary = "인증번호 전송", description = "회원가입용 인증번호 메일 전송")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Verification code sent"),
		@ApiResponse(responseCode = "409", description = "Member with this email already exists")
	})
	@PostMapping("/send-code/verification-code")
	public ResponseEntity<Map<String, String>> sendRegisterVerificationCode(@RequestBody MailVerificationRequest request) {

		String email = request.email();
		Map<String, String> response = new HashMap<>();

		try {
			mailAuthenticationService.sendRegisterVerificationCode(email);
			response.put("message", "Verification code sent to " + email);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (MemberAlreadyExistsException e) {
			response.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}
	}

	@Operation(summary = "인증번호 전송", description = "아이디 찾기용 인증번호 메일 전송")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Verification code sent"),
		@ApiResponse(responseCode = "404", description = "Member not found")
	})
	@PostMapping("/send-code/recovery-code")
	public ResponseEntity<Map<String, String>> sendFindMemberVerificationCode(@RequestBody MailVerificationRequest request) {

		String email = request.email();
		Map<String, String> response = new HashMap<>();

		try {
			mailAuthenticationService.sendFindIdVerificationCode(email);
			response.put("message", "Verification code sent to " + email);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (MemberNotFoundException e) {
			response.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	@Operation(summary = "인증 코드 검증", description = "입력한 인증 코드와 redis에 저장된 인증 코드 비교")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Email verified successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid verification code")
	})
	@PostMapping("/verify-code")
	public ResponseEntity<Map<String, String>> verifyCode(@RequestBody MailVerificationRequest request) {
		Map<String, String> response = new HashMap<>();

		try {
			mailAuthenticationService.verifyCode(request.email(), request.code());
			response.put("message", "Email verified successfully");
			return ResponseEntity.ok(response);
		} catch (InvalidVerificationCodeException e) {
			response.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}
}
