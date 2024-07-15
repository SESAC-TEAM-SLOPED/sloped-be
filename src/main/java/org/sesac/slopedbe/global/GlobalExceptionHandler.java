package org.sesac.slopedbe.global;

import org.sesac.slopedbe.common.exception.BaseException;
import org.sesac.slopedbe.common.exception.ErrorCode;
import org.sesac.slopedbe.common.exception.GlobalErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		return ResponseEntity
			.status(GlobalErrorCode.BAD_REQUEST.getStatus())
			.body(e.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
		return ResponseEntity
			.status(GlobalErrorCode.FORBIDDEN.getStatus())
			.body(ex.getMessage());
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<String> handleBaseException(BaseException ex) {
		ErrorCode errorCode = ex.getErrorCode();
		log.error("사용자정의 Exception 발생, message: {}", errorCode.getMessage());
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(errorCode.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		log.error("예상하지 못한 Error 발생 : {}", e.getMessage(), e);
		return ResponseEntity
			.status(GlobalErrorCode.INTERNAL_SERVER_ERROR.getStatus())
			.body(GlobalErrorCode.INTERNAL_SERVER_ERROR.getMessage());
	}
}
