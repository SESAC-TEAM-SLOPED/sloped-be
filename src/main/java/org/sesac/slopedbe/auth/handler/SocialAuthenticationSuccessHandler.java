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

		String token = jwtUtil.generateToken(userDetails);

		// JWT 토큰을 쿠키에 설정 (리다이렉트 시에 헤더에 authToken을 직접 포함시키는 것은 불가능)
		Cookie cookie = new Cookie("jwtToken", token);
		cookie.setHttpOnly(false); // JavaScript에서 접근 가능하게 설정 (테스트 용도)
		cookie.setSecure(false); // HTTPS가 아닌 환경에서도 전송 가능하게 설정 (테스트 용도)
		cookie.setPath("/"); // 전체 사이트에서 유효
		cookie.setMaxAge(60 * 5); // 쿠키 유효 기간 설정 (5분)

		log.info("Generated token: {}", token);
		response.addCookie(cookie);

		response.sendRedirect("http://localhost:3000/get-jwt");

		log.info("JWT response에 포함");

	}
}
