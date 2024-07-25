package org.sesac.slopedbe.auth.service;

import java.io.IOException;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.auth.model.dto.request.LoginRequest;
import org.sesac.slopedbe.member.model.entity.Member;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletResponse;

public interface TokenAuthenticationService {

	void saveRefreshToken(Member member, String refreshToken);
	boolean validateRefreshToken(Member member, String refreshToken);
	String generateAndSaveRefreshTokenIfNeeded(Member member, GeneralUserDetails userDetails);
	ResponseEntity<?> createAuthenticationToken(LoginRequest loginRequest, HttpServletResponse response) throws	IOException;
	ResponseEntity<?> refreshAccessToken(String refreshTokenHeader, String expiredAccessToken, HttpServletResponse response) throws IOException;
	void createSocialAuthenticationCookies(HttpServletResponse response, GeneralUserDetails userDetails) throws IOException;
}
