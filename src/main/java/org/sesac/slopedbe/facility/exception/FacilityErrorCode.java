package org.sesac.slopedbe.facility.exception;

import org.sesac.slopedbe.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FacilityErrorCode implements ErrorCode {
	FACILITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Facility not found"),
	FACILITY_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "Facility type not found"),
	FIND_FACILITY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "Limit exceeded");

	private final HttpStatus status;
	private final String message;
}
