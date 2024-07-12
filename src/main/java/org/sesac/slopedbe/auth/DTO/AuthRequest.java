package org.sesac.slopedbe.auth.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
	private String id;
	private String password;
}
