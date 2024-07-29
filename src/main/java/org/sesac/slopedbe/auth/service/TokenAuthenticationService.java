package org.sesac.slopedbe.auth.service;

import java.io.IOException;
import java.util.Map;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.auth.model.dto.request.LoginRequest;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletResponse;

public interface TokenAuthenticationService {

	ResponseEntity<Map<String, String>> createAuthenticationToken(LoginRequest loginRequest, HttpServletResponse response) throws	IOException;
	ResponseEntity<Map<String, String>> refreshToken(String refreshTokenHeader, String expiredAccessToken, HttpServletResponse response) throws IOException;
	void createSocialAuthenticationCookies(HttpServletResponse response, GeneralUserDetails userDetails) throws IOException;
}
