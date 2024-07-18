package org.sesac.slopedbe.auth.model.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
	private String memberId;
	private String password;
}
