package org.sesac.slopedbe.roadreport.exception;

import org.sesac.slopedbe.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoadReportErrorCode implements ErrorCode {

	REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 제보입니다."),
	REPORT_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "제보 생성 중 오류가 발생했습니다."),
	REPORT_IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "이미지가 아닌 파일은 업로드할 수 없습니다."),
	GENERAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

	REPORT_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "제보 업데이트 중 오류가 발생했습니다."),
	REPORT_DELETION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "제보 삭제 중 오류가 발생했습니다."),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 입력 값입니다."),

	CENTER_LIST_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "민원기관 리스트를 불러오는 중 오류가 발생했습니다."),
	CLOSEST_CENTER_NOT_FOUND(HttpStatus.NOT_FOUND, "가장 가까운 민원기관을 찾을 수 없습니다."),

	CLOSEST_CALL_TAXI_NOT_FOUND(HttpStatus.NOT_FOUND, "가장 가까운 콜택시를 찾을 수 없습니다."),

	ROAD_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 도로 ID입니다."),

	FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기가 제한을 초과했습니다.");

	private final HttpStatus status;
	private final String message;
}