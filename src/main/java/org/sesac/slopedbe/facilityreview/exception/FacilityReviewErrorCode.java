package org.sesac.slopedbe.facilityreview.exception;

import org.sesac.slopedbe.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FacilityReviewErrorCode implements ErrorCode {
	FACILITY_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),
	REVIEW_IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "이미지가 아닌 파일은 업로드할 수 없습니다."),
	GENERAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String message;
}
