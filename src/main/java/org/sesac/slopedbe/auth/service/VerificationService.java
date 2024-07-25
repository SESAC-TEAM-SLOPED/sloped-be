package org.sesac.slopedbe.auth.service;

import java.io.IOException;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.member.model.entity.Member;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service

public interface VerificationService {

	void verifyCode(String email, String code);
	void sendRegisterVerificationCode(String email);
	void sendFindIdVerificationCode(String email);
	void sendSocialRegisterInformation(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws
		IOException;
	void saveRefreshToken(Member member, String refreshToken);
	boolean validateRefreshToken(Member member, String refreshToken);
	String generateAndSaveRefreshTokenIfNeeded(Member member, GeneralUserDetails userDetails);
}
