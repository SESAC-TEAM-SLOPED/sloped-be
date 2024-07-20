package org.sesac.slopedbe.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	HttpStatus getStatus();
	String getMessage();
}
