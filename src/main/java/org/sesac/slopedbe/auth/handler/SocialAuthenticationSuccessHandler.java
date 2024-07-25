package org.sesac.slopedbe.auth.handler;

import java.io.IOException;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.auth.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SocialAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws
		IOException, ServletException {

		GeneralUserDetails userDetails = (GeneralUserDetails) authentication.getPrincipal();

		String accessToken = jwtUtil.generateAccessToken(userDetails);
		String refreshToken = jwtUtil.generateRefreshToken(userDetails);

		// JWT 토큰을 쿠키에 설정 (리다이렉트 시에 헤더에 authToken을 직접 포함시키는 것은 불가능)
		Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
		accessTokenCookie.setHttpOnly(false); // JavaScript에서 접근 가능하게 설정 (테스트 용도)
		accessTokenCookie.setSecure(false); // HTTPS가 아닌 환경에서도 전송 가능하게 설정 (테스트 용도)
		accessTokenCookie.setPath("/");
		accessTokenCookie.setMaxAge(60 * 5); // 쿠키 유효 기간 설정 (5분)

		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(false); // JavaScript에서 접근 가능하게 설정 (테스트 용도)
		refreshTokenCookie.setSecure(false); // HTTPS가 아닌 환경에서도 전송 가능하게 설정 (테스트 용도)
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(60 * 5); // 쿠키 유효 기간 설정 (5분)

		log.info("Generated access token: {}", accessToken);
		log.info("Generated refresh token: {}", refreshToken);
		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);

		response.sendRedirect("http://localhost:3000/get-jwt");

		log.info("JWT response에 포함");

	}
}
