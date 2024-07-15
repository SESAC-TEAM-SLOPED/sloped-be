package org.sesac.slopedbe.auth.model.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
	private String id;
	private String password;
}
