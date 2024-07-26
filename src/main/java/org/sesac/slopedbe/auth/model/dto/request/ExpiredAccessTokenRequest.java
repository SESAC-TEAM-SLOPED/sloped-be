package org.sesac.slopedbe.auth.model.dto.request;

import lombok.Getter;

@Getter
public class ExpiredAccessTokenRequest {
	private String expiredAccessToken;
}
