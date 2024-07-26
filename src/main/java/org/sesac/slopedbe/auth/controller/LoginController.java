package org.sesac.slopedbe.auth.controller;

import java.io.IOException;

import org.sesac.slopedbe.auth.model.dto.request.LoginRequest;
import org.sesac.slopedbe.auth.service.TokenAuthenticationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class LoginController {

	private final TokenAuthenticationService tokenAuthenticationService;


	@PostMapping(value="/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws
		IOException {
		//Local Login용 method, memberId, password 받아 로그인 진행

		log.info("Login attempt for user: {}", loginRequest.getMemberId());

		return tokenAuthenticationService.createAuthenticationToken(loginRequest, response);
	}

	@GetMapping("/login-success")
	public void loginSuccess(HttpServletResponse response) throws IOException {
		log.info("JWT Client 전달");
		response.sendRedirect("http://localhost:3000/get-jwt");
	}

}
