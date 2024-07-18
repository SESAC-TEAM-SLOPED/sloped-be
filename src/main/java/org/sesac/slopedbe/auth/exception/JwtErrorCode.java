package org.sesac.slopedbe.auth.exception;

import org.sesac.slopedbe.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {

	JWT_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
	JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
	JWT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
	JWT_PREFIX_INVALID(HttpStatus.UNAUTHORIZED, "토큰 접두사가 올바르지 않습니다.");

	private final HttpStatus status;
	private final String message;
}
