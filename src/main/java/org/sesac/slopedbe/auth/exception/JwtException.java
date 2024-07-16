package org.sesac.slopedbe.auth.exception;

import org.sesac.slopedbe.common.exception.BaseException;

public class JwtException extends BaseException {
	public JwtException(JwtErrorCode errorCode) {
		super(errorCode);
	}
}
