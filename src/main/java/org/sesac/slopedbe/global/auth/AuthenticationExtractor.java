package org.sesac.slopedbe.global.auth;

import org.sesac.slopedbe.auth.exception.JwtErrorCode;
import org.sesac.slopedbe.auth.exception.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AuthenticationExtractor {

	public String extract(NativeWebRequest request) {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			throw new JwtException(JwtErrorCode.JWT_NOT_FOUND);
		}
		if (!authorizationHeader.startsWith("Bearer")) {
			throw new JwtException(JwtErrorCode.JWT_PREFIX_INVALID);
		}
		return authorizationHeader.replace("Bearer", "").trim();
	}
}
