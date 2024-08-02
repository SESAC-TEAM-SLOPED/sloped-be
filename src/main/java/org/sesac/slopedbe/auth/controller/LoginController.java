package org.sesac.slopedbe.auth.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.sesac.slopedbe.auth.model.dto.request.LoginRequest;
import org.sesac.slopedbe.auth.service.TokenAuthenticationService;
import org.sesac.slopedbe.member.model.dto.request.MemberRequest;
import org.sesac.slopedbe.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class LoginController {

	private final TokenAuthenticationService tokenAuthenticationService;
	private final MemberService memberService;

	@Operation(summary = "Local login", description = "memberId, password 받아 로그인 진행 Token을 Cookie 형태로 발급한다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그인 성공, Token 발급 완료"),
		@ApiResponse(responseCode = "401", description = "잘못된 자격 증명"),
		@ApiResponse(responseCode = "401", description = "계정 잠김"),
		@ApiResponse(responseCode = "401", description = "계정 비활성화"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 회원")
	})
	@PostMapping(value="/login")
	public ResponseEntity<Map<String, String>> createAuthenticationToken(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws
		IOException {

		log.info("Login attempt for user: {}", loginRequest.memberId());
		return tokenAuthenticationService.createAuthenticationToken(loginRequest, response);
	}

	@Operation(summary = "회원 가입", description = "소셜 유저 정보를 DB에 저장한다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "소셜 유저 가입 성공")
	})
	@PostMapping("/register/social")
	public ResponseEntity<Map<String, String>> registerSocialMember(@RequestBody MemberRequest memberRequest) {
		memberService.registerSocialMember(memberRequest);
		Map<String, String> response = new HashMap<>();
		response.put("message", "소셜 유저 가입 성공");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
