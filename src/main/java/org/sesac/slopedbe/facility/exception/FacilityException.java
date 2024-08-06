package org.sesac.slopedbe.facility.exception;

import org.sesac.slopedbe.common.exception.BaseException;

public class FacilityException extends BaseException {
	public FacilityException(FacilityErrorCode errorCode) {
		super(errorCode);
	}
}
