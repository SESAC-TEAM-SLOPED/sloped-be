package org.sesac.slopedbe.auth.controller;

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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class MailController {

	private final MailAuthenticationService mailAuthenticationService;

	@PostMapping("/send-code/verification-code")
	public ResponseEntity<String> sendRegisterVerificationCode(@RequestBody MailVerificationRequest request) {
		// 회원가입용 인증번호 메일 전송

		String email = request.email();

		log.info("Received request to send verification code to: {}", email);

		try {
			mailAuthenticationService.sendRegisterVerificationCode(email);
			return ResponseEntity.status(HttpStatus.CREATED).body("Verification code sent to " + email);
		} catch (MemberAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/send-code/recovery-code")
	public ResponseEntity<String> sendFindMemberVerificationCode(@RequestBody MailVerificationRequest request) {
		// Client에서 아이디 찾기, 비밀번호 찾기 중 이메일 인증 요청이 오면 메일로 인증코드 전송 (gmail 활용)


		String email = request.email();

		try {
			mailAuthenticationService.sendFindIdVerificationCode(email);
			log.info("Received request to send verification code to: {}", email);
			return ResponseEntity.status(HttpStatus.CREATED).body("Verification code sent to " + email);
		} catch (MemberNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/verify-code")
	public ResponseEntity<String> verifyCode(@RequestBody MailVerificationRequest request) {
		// 저장된 인증 코드와 request 코드 검증 method
		mailAuthenticationService.verifyCode(request.email(), request.code());
		return ResponseEntity.ok("Email verified successfully");
	}
}
