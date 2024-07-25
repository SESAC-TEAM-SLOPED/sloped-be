package org.sesac.slopedbe.global;

import java.util.stream.Collectors;

import org.sesac.slopedbe.common.exception.BaseException;
import org.sesac.slopedbe.common.exception.ErrorCode;
import org.sesac.slopedbe.common.exception.GlobalErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		String errorMessage = ex.getBindingResult().getAllErrors().stream()
			.map(error -> {
				if (error instanceof FieldError) {
					return ((FieldError) error).getField() + ": " + error.getDefaultMessage();
				}
				return error.getDefaultMessage();
			})
			.collect(Collectors.joining(", "));

		return ResponseEntity
			.status(GlobalErrorCode.BAD_REQUEST.getStatus())
			.body(errorMessage);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		return ResponseEntity.status(GlobalErrorCode.METHOD_NOT_ALLOWED.getStatus()).body(ex.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
		return ResponseEntity
			.status(GlobalErrorCode.FORBIDDEN.getStatus())
			.body(ex.getMessage());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
	}

	@ExceptionHandler(LockedException.class)
	public ResponseEntity<String> handleLockedException(LockedException ex) {
		return ResponseEntity.status(HttpStatus.LOCKED).body("Account locked");
	}

	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<String> handleDisabledException(DisabledException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account disabled");
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed");
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
