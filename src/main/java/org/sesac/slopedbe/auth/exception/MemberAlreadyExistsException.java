package org.sesac.slopedbe.auth.exception;

public class MemberAlreadyExistsException extends RuntimeException {
	public MemberAlreadyExistsException(String message) {
		super(message);
	}
}
