package org.sesac.slopedbe.auth.handler;

import java.io.IOException;

import org.sesac.slopedbe.auth.service.LoginServiceImpl;
import org.sesac.slopedbe.auth.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

	private final JwtUtil jwtUtil;
	private final LoginServiceImpl memberService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
		String userName = oauthToken.getPrincipal().getName();

		UserDetails userDetails = memberService.loadUserByUsername(userName);
		String jwt = jwtUtil.generateToken(userDetails.getUsername());

		log.info("JWT generated for user {}: {}", userName, jwt);

		response.addHeader("Authorization", "Bearer " + jwt);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"jwt\": \"" + jwt + "\"}");

		String targetUrl = determineTargetUrl(request, response, authentication);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
