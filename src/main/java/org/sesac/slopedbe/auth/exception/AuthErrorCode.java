package org.sesac.slopedbe.auth.exception;

import org.sesac.slopedbe.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
	INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 인증 코드입니다."),
	BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "잘못된 자격 증명입니다."),
	ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "계정이 잠겼습니다."),
	ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED, "계정이 비활성화되었습니다."),
	AUTHENTICATION_EXCEPTION(HttpStatus.UNAUTHORIZED, "인증 예외가 발생했습니다.");

	private final HttpStatus status;
	private final String message;
}
