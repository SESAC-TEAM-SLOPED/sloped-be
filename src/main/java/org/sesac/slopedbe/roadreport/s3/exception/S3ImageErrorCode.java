package org.sesac.slopedbe.roadreport.s3.exception;

import org.sesac.slopedbe.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3ImageErrorCode implements ErrorCode {
	S3_FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기 제한을 초과했습니다.");

	private final HttpStatus status;
	private final String message;
}
