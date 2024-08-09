package org.sesac.slopedbe.facilityreview.exception;

import org.sesac.slopedbe.common.exception.BaseException;

public class FacilityReviewException extends BaseException {
	public FacilityReviewException(FacilityReviewErrorCode errorCode) { super(errorCode); }
}
