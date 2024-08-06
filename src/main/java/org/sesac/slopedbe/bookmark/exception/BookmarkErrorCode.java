package org.sesac.slopedbe.bookmark.exception;

import org.sesac.slopedbe.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookmarkErrorCode implements ErrorCode {
	BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 시설은 이미 회원의 즐겨찾기 목록에 있습니다.");

	private final HttpStatus status;
	private final String message;
}
