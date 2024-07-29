package org.sesac.slopedbe.auth.controller;

import java.io.IOException;
import java.util.Map;

import org.sesac.slopedbe.auth.service.TokenAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class JwtController {

	private final TokenAuthenticationService tokenAuthenticationService;

	@Operation(summary = "Token 갱신", description = "Access Token, Refresh Token을 갱신한다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Access token and refresh token refreshed"),
		@ApiResponse(responseCode = "401", description = "Invalid refresh token"),
		@ApiResponse(responseCode = "404", description = "Member not found")
	})
	@PostMapping(value = "/refresh-token")
	public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Cookie expiredAccessTokenCookie = WebUtils.getCookie(request, "expiredAccessToken");
		Cookie refreshTokenCookie = WebUtils.getCookie(request, "refreshToken");

		String expiredAccessToken = expiredAccessTokenCookie.getValue();
		String refreshToken = refreshTokenCookie.getValue();

		return tokenAuthenticationService.refreshToken(expiredAccessToken, refreshToken ,response);
	}

}
