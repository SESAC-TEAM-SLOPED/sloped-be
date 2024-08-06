package org.sesac.slopedbe.bookmark.exception;

import org.sesac.slopedbe.common.exception.BaseException;

public class BookmarkException extends BaseException {
	public BookmarkException(BookmarkErrorCode errorCode) {
		super(errorCode);
	}
}
