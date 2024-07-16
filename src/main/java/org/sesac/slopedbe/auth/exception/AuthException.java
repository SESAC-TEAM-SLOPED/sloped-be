package org.sesac.slopedbe.auth.exception;

import org.sesac.slopedbe.common.exception.BaseException;

public class AuthException extends BaseException {
	public AuthException(AuthErrorCode errorCode) {
		super(errorCode);
	}
}
