package org.sesac.slopedbe.facility.exception;

import org.sesac.slopedbe.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FacilityErrorCode implements ErrorCode {
	FACILITY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 시설입니다."),
	FACILITY_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "시설 타입은 RESTAURANT, CAFE, PUBLIC_SPACE, ETC 중 하나여야 합니다."),
	FIND_FACILITY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "시설은 100개 이하로 조회 가능합니다."),;

	private final HttpStatus status;
	private final String message;
}
