package org.sesac.slopedbe.auth.controller;

import java.io.IOException;
import java.util.Map;

import org.sesac.slopedbe.auth.model.dto.request.LoginRequest;
import org.sesac.slopedbe.auth.service.TokenAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

	@Operation(summary = "Local login", description = "memberId, password 받아 로그인 진행, Token 발급")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Login successful, tokens issued"),
		@ApiResponse(responseCode = "401", description = "Invalid credentials"),
		@ApiResponse(responseCode = "404", description = "Member not found")
	})
	@PostMapping(value="/login")
	public ResponseEntity<Map<String, String>> createAuthenticationToken(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws
		IOException {

		log.info("Login attempt for user: {}", loginRequest.getMemberId());
		return tokenAuthenticationService.createAuthenticationToken(loginRequest, response);
	}


	@GetMapping("/login-success")
	public void loginSuccess(HttpServletResponse response) throws IOException {
		log.info("JWT Client 전달");
		response.sendRedirect("http://localhost:3000/get-jwt");
	}

}
