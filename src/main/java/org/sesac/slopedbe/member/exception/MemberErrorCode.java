package org.sesac.slopedbe.member.exception;

import org.sesac.slopedbe.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

	MEMBER_ID_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 회원 아이디입니다."),
	MEMBER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 회원 이메일입니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
	MEMBER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원 아이디입니다.");

	private final HttpStatus status;
	private final String message;
}
