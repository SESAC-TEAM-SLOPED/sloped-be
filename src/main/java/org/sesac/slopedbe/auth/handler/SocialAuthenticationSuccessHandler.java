package org.sesac.slopedbe.auth.handler;

import java.io.IOException;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.auth.service.TokenAuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SocialAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final TokenAuthenticationService tokenAuthenticationService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws
		IOException, ServletException {

		GeneralUserDetails userDetails = (GeneralUserDetails) authentication.getPrincipal();

		tokenAuthenticationService.createSocialAuthenticationCookies(response, userDetails);

		response.sendRedirect("http://localhost:3000/get-jwt");
	}
}
