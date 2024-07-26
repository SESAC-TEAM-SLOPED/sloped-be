package org.sesac.slopedbe.auth.controller;

import java.io.IOException;

import org.sesac.slopedbe.auth.model.dto.request.ExpiredAccessTokenRequest;
import org.sesac.slopedbe.auth.service.TokenAuthenticationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class JwtController {

	private final TokenAuthenticationService tokenAuthenticationService;

	@PostMapping(value = "/refresh-access-token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshTokenHeader, @RequestBody ExpiredAccessTokenRequest expiredAccessTokenRequest, HttpServletResponse response) throws IOException {
		log.info("access-token 요청");
		String expiredAccessToken = expiredAccessTokenRequest.getExpiredAccessToken();
		return tokenAuthenticationService.refreshAccessToken(refreshTokenHeader, expiredAccessToken, response);
	}
}
