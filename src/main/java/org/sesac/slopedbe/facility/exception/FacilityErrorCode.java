package org.sesac.slopedbe.facility.exception;

import org.sesac.slopedbe.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FacilityErrorCode implements ErrorCode {
	FACILITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Facility not found");

	private final HttpStatus status;
	private final String message;
}
