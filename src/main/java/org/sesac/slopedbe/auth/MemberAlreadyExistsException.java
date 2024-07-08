package org.sesac.slopedbe.auth;

public class MemberAlreadyExistsException extends RuntimeException {
	public MemberAlreadyExistsException(String message) {
		super(message);
	}
}

