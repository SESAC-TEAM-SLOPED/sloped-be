package org.sesac.slopedbe.roadreport.exception;

import org.sesac.slopedbe.common.exception.BaseException;

public class RoadReportException extends BaseException {
	public RoadReportException(RoadReportErrorCode errorCode) {
		super(errorCode);
	}
}