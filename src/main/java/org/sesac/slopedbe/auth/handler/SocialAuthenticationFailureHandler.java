package org.sesac.slopedbe.auth.handler;

import java.io.IOException;

import org.sesac.slopedbe.auth.service.VerificationService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SocialAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final VerificationService verificationService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

		verificationService.sendSocialRegisterInformation(request, response, exception);
	}
}