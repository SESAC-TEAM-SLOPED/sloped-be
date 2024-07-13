package org.sesac.slopedbe.auth.handler;

import java.io.IOException;

import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.member.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

	private final JwtUtil jwtUtil;
	private final MemberService memberService;

	public OAuth2SuccessHandler(JwtUtil jwtUtil, MemberService memberService) {
		this.jwtUtil = jwtUtil;
		this.memberService = memberService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
		String userName = oauthToken.getPrincipal().getName();

		// 사용자 정보를 가져와서 JWT 생성
		UserDetails userDetails = memberService.loadUserByUsername(userName);
		String jwt = jwtUtil.generateToken(userDetails.getUsername());

		log.info("JWT generated for user {}: {}", userName, jwt);

		// JWT를 응답 헤더에 추가
		response.addHeader("Authorization", "Bearer " + jwt);

		// JSON 응답으로 JWT 반환
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"jwt\": \"" + jwt + "\"}");

		// 성공 후 리디렉션할 URL 설정
		String targetUrl = determineTargetUrl(request, response, authentication);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
