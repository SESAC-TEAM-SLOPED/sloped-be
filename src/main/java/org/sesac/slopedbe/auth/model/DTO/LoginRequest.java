package org.sesac.slopedbe.auth.model.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
	private String id;
	private String password;
}
