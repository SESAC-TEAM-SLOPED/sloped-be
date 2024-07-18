package org.sesac.slopedbe.member.exception;

import org.sesac.slopedbe.common.exception.BaseException;

public class MemberException extends BaseException {
	public MemberException(MemberErrorCode errorCode) {
		super(errorCode);
	}
}
